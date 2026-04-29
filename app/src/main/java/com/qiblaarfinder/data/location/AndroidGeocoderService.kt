package com.qiblaarfinder.data.location

import android.content.Context
import android.location.Geocoder
import com.qiblaarfinder.domain.model.LocationPoint
import com.qiblaarfinder.domain.model.LocationSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

class AndroidGeocoderService(context: Context) {
    private val geocoder = Geocoder(context, Locale.getDefault())

    suspend fun searchCity(query: String): LocationPoint? = withContext(Dispatchers.IO) {
        if (!Geocoder.isPresent()) return@withContext null

        @Suppress("DEPRECATION")
        val addresses = geocoder.getFromLocationName(query, 1).orEmpty()
        val best = addresses.firstOrNull() ?: return@withContext null

        LocationPoint(
            latitude = best.latitude,
            longitude = best.longitude,
            cityName = best.locality ?: best.subAdminArea ?: best.adminArea ?: query,
            source = LocationSource.MANUAL,
        )
    }

    suspend fun reverseGeocode(latitude: Double, longitude: Double): String? = withContext(Dispatchers.IO) {
        if (!Geocoder.isPresent()) return@withContext null

        @Suppress("DEPRECATION")
        val addresses = geocoder.getFromLocation(latitude, longitude, 1).orEmpty()
        val best = addresses.firstOrNull() ?: return@withContext null

        best.locality ?: best.subAdminArea ?: best.adminArea ?: best.countryName
    }
}

