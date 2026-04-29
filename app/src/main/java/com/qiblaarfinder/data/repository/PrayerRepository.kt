package com.qiblaarfinder.data.repository

import com.qiblaarfinder.data.db.PrayerScheduleDao
import com.qiblaarfinder.data.db.PrayerScheduleEntity
import com.qiblaarfinder.domain.model.LocationPoint
import com.qiblaarfinder.domain.model.PrayerSchedule
import com.qiblaarfinder.domain.util.PrayerTimesCalculator
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class PrayerRepository(
    private val prayerScheduleDao: PrayerScheduleDao,
    private val calculator: PrayerTimesCalculator,
) {
    private val formatter = DateTimeFormatter.ofPattern("HH:mm")

    suspend fun getPrayerSchedule(
        locationPoint: LocationPoint,
        date: LocalDate = LocalDate.now(),
        zoneId: ZoneId = ZoneId.systemDefault(),
    ): PrayerSchedule {
        val cacheKey = buildCacheKey(locationPoint, date)
        val cached = prayerScheduleDao.findByCacheKey(cacheKey)
        if (cached != null) {
            return cached.toModel()
        }

        val calculated = calculator.calculate(
            latitude = locationPoint.latitude,
            longitude = locationPoint.longitude,
            date = date,
            zoneId = zoneId,
        )

        prayerScheduleDao.upsert(
            PrayerScheduleEntity(
                cacheKey = cacheKey,
                date = date.toString(),
                latitude = locationPoint.latitude,
                longitude = locationPoint.longitude,
                cityName = locationPoint.cityName,
                fajr = calculated.formatted(calculated.fajr),
                sunrise = calculated.formatted(calculated.sunrise),
                dhuhr = calculated.formatted(calculated.dhuhr),
                asr = calculated.formatted(calculated.asr),
                maghrib = calculated.formatted(calculated.maghrib),
                isha = calculated.formatted(calculated.isha),
                updatedAt = System.currentTimeMillis(),
            ),
        )

        return calculated
    }

    fun buildCacheKey(locationPoint: LocationPoint, date: LocalDate = LocalDate.now()): String {
        val roundedLatitude = String.format(Locale.US, "%.3f", locationPoint.latitude)
        val roundedLongitude = String.format(Locale.US, "%.3f", locationPoint.longitude)
        return "${date}_${roundedLatitude}_${roundedLongitude}"
    }

    private fun PrayerScheduleEntity.toModel(): PrayerSchedule = PrayerSchedule(
        date = LocalDate.parse(date),
        fajr = LocalTime.parse(fajr, formatter),
        sunrise = LocalTime.parse(sunrise, formatter),
        dhuhr = LocalTime.parse(dhuhr, formatter),
        asr = LocalTime.parse(asr, formatter),
        maghrib = LocalTime.parse(maghrib, formatter),
        isha = LocalTime.parse(isha, formatter),
    )
}
