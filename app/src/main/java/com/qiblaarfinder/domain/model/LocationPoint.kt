package com.qiblaarfinder.domain.model

data class LocationPoint(
    val latitude: Double,
    val longitude: Double,
    val cityName: String,
    val source: LocationSource,
    val updatedAt: Long = System.currentTimeMillis(),
)

