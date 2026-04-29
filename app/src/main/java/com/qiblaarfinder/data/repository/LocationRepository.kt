package com.qiblaarfinder.data.repository

import com.qiblaarfinder.data.db.CachedLocationEntity
import com.qiblaarfinder.data.db.LocationDao
import com.qiblaarfinder.data.location.AndroidGeocoderService
import com.qiblaarfinder.data.location.AndroidLocationClient
import com.qiblaarfinder.domain.model.LocationPoint
import com.qiblaarfinder.domain.model.LocationSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocationRepository(
    private val locationDao: LocationDao,
    private val locationClient: AndroidLocationClient,
    private val geocoderService: AndroidGeocoderService,
) {
    fun observeCachedLocation(): Flow<LocationPoint?> = locationDao.observeLatest().map { entity ->
        entity?.toModel()
    }

    suspend fun refreshCurrentLocation(): Result<LocationPoint> = runCatching {
        val currentLocation = locationClient.getCurrentLocation()
            ?: error("Lokasi GPS belum tersedia. Coba pindah ke area terbuka lalu refresh lagi.")

        val resolvedCity = geocoderService.reverseGeocode(
            latitude = currentLocation.latitude,
            longitude = currentLocation.longitude,
        )

        val enriched = currentLocation.copy(
            cityName = resolvedCity ?: currentLocation.cityName,
            source = LocationSource.AUTO,
        )

        locationDao.upsert(CachedLocationEntity.fromModel(enriched))
        enriched
    }

    suspend fun searchCity(query: String): Result<LocationPoint> = runCatching {
        val location = geocoderService.searchCity(query)
            ?: error("Kota tidak ditemukan. Coba pakai nama kota yang lebih spesifik.")

        val manualPoint = location.copy(source = LocationSource.MANUAL)
        locationDao.upsert(CachedLocationEntity.fromModel(manualPoint))
        manualPoint
    }

    suspend fun saveManualCoordinates(
        cityName: String,
        latitude: Double,
        longitude: Double,
    ): Result<LocationPoint> = runCatching {
        val manualPoint = LocationPoint(
            latitude = latitude,
            longitude = longitude,
            cityName = cityName.ifBlank { "Titik Manual" },
            source = LocationSource.MANUAL,
        )

        locationDao.upsert(CachedLocationEntity.fromModel(manualPoint))
        manualPoint
    }
}
