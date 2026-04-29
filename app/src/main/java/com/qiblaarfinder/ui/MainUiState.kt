package com.qiblaarfinder.ui

import com.qiblaarfinder.domain.model.LocationPoint
import com.qiblaarfinder.domain.model.PrayerSchedule
import kotlin.math.abs

data class MainUiState(
    val currentLocation: LocationPoint? = null,
    val prayerSchedule: PrayerSchedule? = null,
    val hasLocationPermission: Boolean = false,
    val deviceHeading: Float = 0f,
    val qiblaBearing: Float? = null,
    val directionDelta: Float? = null,
    val isLoadingLocation: Boolean = false,
    val isLoadingPrayerSchedule: Boolean = false,
    val message: String? = null,
) {
    val isAligned: Boolean
        get() = directionDelta?.let { abs(it) <= 2f } == true
}

