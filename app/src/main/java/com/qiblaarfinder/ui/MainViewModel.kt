package com.qiblaarfinder.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.qiblaarfinder.data.repository.LocationRepository
import com.qiblaarfinder.data.repository.PrayerRepository
import com.qiblaarfinder.data.sensor.CompassSensorManager
import com.qiblaarfinder.di.AppContainer
import com.qiblaarfinder.domain.model.LocationPoint
import com.qiblaarfinder.domain.model.LocationSource
import com.qiblaarfinder.domain.util.CompassMath
import com.qiblaarfinder.domain.util.LocationInputValidator
import com.qiblaarfinder.domain.util.QiblaCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainViewModel(
    private val locationRepository: LocationRepository,
    private val prayerRepository: PrayerRepository,
    private val compassSensorManager: CompassSensorManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    private var lastPermissionState: Boolean? = null
    private var loadedPrayerKey: String? = null
    private var hydratedCachedLocation = false

    init {
        observeCompass()
        observeCachedLocation()
    }

    fun onLocationPermissionChanged(granted: Boolean) {
        val changed = lastPermissionState != granted
        lastPermissionState = granted

        _uiState.update { it.copy(hasLocationPermission = granted) }

        if (granted && (changed || _uiState.value.currentLocation == null)) {
            refreshCurrentLocation()
        } else if (!granted && changed && _uiState.value.currentLocation == null) {
            pushMessage("Izin lokasi belum diaktifkan. Gunakan input manual atau cache terakhir.")
        }
    }

    fun refreshCurrentLocation() {
        if (!_uiState.value.hasLocationPermission) {
            pushMessage("Aktifkan izin lokasi dulu untuk update GPS otomatis.")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingLocation = true) }
            locationRepository.refreshCurrentLocation()
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoadingLocation = false) }
                    pushMessage(throwable.message ?: "Gagal mengambil lokasi.")
                }
                .onSuccess { locationPoint ->
                    applyLocation(locationPoint)
                    _uiState.update { state -> state.copy(isLoadingLocation = false) }
                    pushMessage("Lokasi berhasil diperbarui dari GPS.")
                }
        }
    }

    fun saveManualCoordinates(cityName: String, latitude: String, longitude: String) {
        val parsedLatitude = latitude.trim().toDoubleOrNull()
        val parsedLongitude = longitude.trim().toDoubleOrNull()

        if (parsedLatitude == null || parsedLongitude == null) {
            pushMessage("Koordinat belum valid. Gunakan format angka desimal.")
            return
        }

        LocationInputValidator.validate(
            latitude = parsedLatitude,
            longitude = parsedLongitude,
        )?.let { validationMessage ->
            pushMessage(validationMessage)
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingLocation = true) }
            locationRepository.saveManualCoordinates(
                cityName = cityName.trim(),
                latitude = parsedLatitude,
                longitude = parsedLongitude,
            ).onFailure { throwable ->
                _uiState.update { it.copy(isLoadingLocation = false) }
                pushMessage(throwable.message ?: "Gagal menyimpan koordinat manual.")
            }.onSuccess { locationPoint ->
                applyLocation(locationPoint)
                _uiState.update { it.copy(isLoadingLocation = false) }
                pushMessage("Lokasi manual disimpan untuk mode offline.")
            }
        }
    }

    fun searchCity(query: String) {
        val normalizedQuery = query.trim()
        if (normalizedQuery.isBlank()) {
            pushMessage("Masukkan nama kota yang mau dicari.")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingLocation = true) }
            locationRepository.searchCity(normalizedQuery)
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoadingLocation = false) }
                    pushMessage(throwable.message ?: "Pencarian kota gagal.")
                }
                .onSuccess { locationPoint ->
                    applyLocation(locationPoint)
                    _uiState.update { it.copy(isLoadingLocation = false) }
                    pushMessage("Kota ditemukan dan dijadikan titik kiblat aktif.")
                }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    private fun observeCompass() {
        viewModelScope.launch {
            compassSensorManager.headingFlow()
                .catch { throwable ->
                    pushMessage(throwable.message ?: "Sensor kompas tidak tersedia.")
                }
                .collectLatest { heading ->
                    _uiState.update { state ->
                        val delta = state.qiblaBearing?.let { bearing ->
                            CompassMath.shortestDelta(
                                target = bearing.toDouble(),
                                current = heading.toDouble(),
                            ).toFloat()
                        }

                        state.copy(
                            deviceHeading = heading,
                            directionDelta = delta,
                        )
                    }
                }
        }
    }

    private fun observeCachedLocation() {
        viewModelScope.launch {
            locationRepository.observeCachedLocation().collectLatest { locationPoint ->
                if (locationPoint != null) {
                    val state = _uiState.value
                    val shouldHydrateFromCache = !hydratedCachedLocation && state.currentLocation == null
                    hydratedCachedLocation = true

                    when {
                        shouldHydrateFromCache -> applyLocation(
                            locationPoint.copy(source = LocationSource.CACHE),
                        )

                        locationPoint != state.currentLocation -> applyLocation(locationPoint)
                    }
                }
            }
        }
    }

    private fun applyLocation(locationPoint: LocationPoint) {
        val qiblaBearing = QiblaCalculator.bearingFrom(
            latitude = locationPoint.latitude,
            longitude = locationPoint.longitude,
        ).toFloat()
        val prayerKey = prayerRepository.buildCacheKey(locationPoint, LocalDate.now())
        val shouldResetSchedule = prayerKey != loadedPrayerKey

        _uiState.update { state ->
            state.copy(
                currentLocation = locationPoint,
                prayerSchedule = if (shouldResetSchedule) null else state.prayerSchedule,
                qiblaBearing = qiblaBearing,
                directionDelta = CompassMath.shortestDelta(
                    target = qiblaBearing.toDouble(),
                    current = state.deviceHeading.toDouble(),
                ).toFloat(),
            )
        }

        refreshPrayerSchedule(locationPoint)
    }

    private fun refreshPrayerSchedule(locationPoint: LocationPoint) {
        val cacheKey = prayerRepository.buildCacheKey(locationPoint, LocalDate.now())
        if (cacheKey == loadedPrayerKey && _uiState.value.prayerSchedule != null) return
        loadedPrayerKey = cacheKey

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingPrayerSchedule = true) }

            runCatching {
                prayerRepository.getPrayerSchedule(locationPoint)
            }.onFailure { throwable ->
                _uiState.update { it.copy(isLoadingPrayerSchedule = false) }
                pushMessage(throwable.message ?: "Gagal memuat jadwal salat.")
            }.onSuccess { schedule ->
                _uiState.update {
                    it.copy(
                        prayerSchedule = schedule,
                        isLoadingPrayerSchedule = false,
                    )
                }
            }
        }
    }

    private fun pushMessage(message: String) {
        _uiState.update { it.copy(message = message) }
    }

    class Factory(private val appContainer: AppContainer) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(
                locationRepository = appContainer.locationRepository,
                prayerRepository = appContainer.prayerRepository,
                compassSensorManager = appContainer.compassSensorManager,
            ) as T
        }
    }
}
