package com.qiblaarfinder.di

import android.content.Context
import androidx.room.Room
import com.qiblaarfinder.data.db.QiblaDatabase
import com.qiblaarfinder.data.location.AndroidGeocoderService
import com.qiblaarfinder.data.location.AndroidLocationClient
import com.qiblaarfinder.data.repository.LocationRepository
import com.qiblaarfinder.data.repository.PrayerRepository
import com.qiblaarfinder.data.sensor.CompassSensorManager
import com.qiblaarfinder.domain.util.PrayerTimesCalculator

class AppContainer(context: Context) {
    private val appContext = context.applicationContext

    private val database by lazy {
        Room.databaseBuilder(
            appContext,
            QiblaDatabase::class.java,
            "qibla_ar_finder.db",
        ).fallbackToDestructiveMigration().build()
    }

    private val geocoderService by lazy {
        AndroidGeocoderService(appContext)
    }

    private val locationClient by lazy {
        AndroidLocationClient(appContext)
    }

    val compassSensorManager by lazy {
        CompassSensorManager(appContext)
    }

    val locationRepository by lazy {
        LocationRepository(
            locationDao = database.locationDao(),
            locationClient = locationClient,
            geocoderService = geocoderService,
        )
    }

    val prayerRepository by lazy {
        PrayerRepository(
            prayerScheduleDao = database.prayerScheduleDao(),
            calculator = PrayerTimesCalculator(),
        )
    }
}

