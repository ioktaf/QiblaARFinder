package com.qiblaarfinder.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PrayerScheduleDao {
    @Query("SELECT * FROM prayer_schedule WHERE cacheKey = :cacheKey LIMIT 1")
    suspend fun findByCacheKey(cacheKey: String): PrayerScheduleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: PrayerScheduleEntity)
}

