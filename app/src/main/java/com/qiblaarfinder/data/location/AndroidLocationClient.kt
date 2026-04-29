package com.qiblaarfinder.data.location

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.qiblaarfinder.domain.model.LocationPoint
import com.qiblaarfinder.domain.model.LocationSource
import kotlinx.coroutines.tasks.await

class AndroidLocationClient(context: Context) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): LocationPoint? {
        val tokenSource = CancellationTokenSource()
        val currentLocation = fusedLocationClient
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, tokenSource.token)
            .await()
            ?: fusedLocationClient.lastLocation.await()
            ?: return null

        return LocationPoint(
            latitude = currentLocation.latitude,
            longitude = currentLocation.longitude,
            cityName = "Lokasi Saat Ini",
            source = LocationSource.AUTO,
        )
    }
}

