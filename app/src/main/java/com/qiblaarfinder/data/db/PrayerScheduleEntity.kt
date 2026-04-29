package com.qiblaarfinder.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prayer_schedule")
data class PrayerScheduleEntity(
    @PrimaryKey val cacheKey: String,
    val date: String,
    val latitude: Double,
    val longitude: Double,
    val cityName: String,
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String,
    val updatedAt: Long,
)

