package com.qiblaarfinder.domain.util

object LocationInputValidator {
    fun validate(latitude: Double, longitude: Double): String? {
        return when {
            latitude !in -90.0..90.0 -> "Latitude harus berada di antara -90 sampai 90."
            longitude !in -180.0..180.0 -> "Longitude harus berada di antara -180 sampai 180."
            else -> null
        }
    }
}
