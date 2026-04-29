package com.qiblaarfinder.domain.util

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

object QiblaCalculator {
    private const val KAABA_LATITUDE = 21.422487
    private const val KAABA_LONGITUDE = 39.826206

    fun bearingFrom(latitude: Double, longitude: Double): Double {
        val latitudeRad = Math.toRadians(latitude)
        val kaabaLatitudeRad = Math.toRadians(KAABA_LATITUDE)
        val deltaLongitude = Math.toRadians(KAABA_LONGITUDE - longitude)

        val y = sin(deltaLongitude)
        val x = cos(latitudeRad) * tan(kaabaLatitudeRad) - sin(latitudeRad) * cos(deltaLongitude)
        val bearingDegrees = Math.toDegrees(atan2(y, x))
        return CompassMath.normalize360(bearingDegrees)
    }
}

