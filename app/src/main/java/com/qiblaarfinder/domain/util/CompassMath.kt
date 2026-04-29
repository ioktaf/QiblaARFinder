package com.qiblaarfinder.domain.util

object CompassMath {
    fun normalize360(degrees: Double): Double {
        val normalized = degrees % 360.0
        return if (normalized < 0) normalized + 360.0 else normalized
    }

    fun shortestDelta(target: Double, current: Double): Double {
        val delta = normalize360(target) - normalize360(current)
        return ((delta + 540.0) % 360.0) - 180.0
    }
}

