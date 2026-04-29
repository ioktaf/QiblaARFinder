package com.qiblaarfinder.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        CachedLocationEntity::class,
        PrayerScheduleEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class QiblaDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
    abstract fun prayerScheduleDao(): PrayerScheduleDao
}

