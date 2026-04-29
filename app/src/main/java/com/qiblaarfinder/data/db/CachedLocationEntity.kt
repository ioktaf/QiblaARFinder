package com.qiblaarfinder.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.qiblaarfinder.domain.model.LocationPoint
import com.qiblaarfinder.domain.model.LocationSource

@Entity(tableName = "cached_location")
data class CachedLocationEntity(
    @PrimaryKey val id: Int = 0,
    val latitude: Double,
    val longitude: Double,
    val cityName: String,
    val source: String,
    val updatedAt: Long,
) {
    fun toModel(): LocationPoint = LocationPoint(
        latitude = latitude,
        longitude = longitude,
        cityName = cityName,
        source = LocationSource.valueOf(source),
        updatedAt = updatedAt,
    )

    companion object {
        fun fromModel(location: LocationPoint): CachedLocationEntity = CachedLocationEntity(
            latitude = location.latitude,
            longitude = location.longitude,
            cityName = location.cityName,
            source = location.source.name,
            updatedAt = location.updatedAt,
        )
    }
}

