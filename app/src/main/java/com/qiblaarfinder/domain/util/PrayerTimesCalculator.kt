package com.qiblaarfinder.domain.util

import com.qiblaarfinder.domain.model.PrayerSchedule
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

class PrayerTimesCalculator(
    private val fajrAngle: Double = 18.0,
    private val ishaAngle: Double = 17.0,
) {
    fun calculate(
        latitude: Double,
        longitude: Double,
        date: LocalDate,
        zoneId: ZoneId = ZoneId.systemDefault(),
    ): PrayerSchedule {
        val dayOfYear = date.dayOfYear
        val gamma = 2.0 * Math.PI / 365.0 * (dayOfYear - 1)

        val equationOfTime = 229.18 * (
            0.000075 +
                0.001868 * cos(gamma) -
                0.032077 * sin(gamma) -
                0.014615 * cos(2.0 * gamma) -
                0.040849 * sin(2.0 * gamma)
            )

        val solarDeclination =
            0.006918 -
                0.399912 * cos(gamma) +
                0.070257 * sin(gamma) -
                0.006758 * cos(2.0 * gamma) +
                0.000907 * sin(2.0 * gamma) -
                0.002697 * cos(3.0 * gamma) +
                0.00148 * sin(3.0 * gamma)

        val zoneOffsetHours = ZonedDateTime.of(date.atStartOfDay(), zoneId).offset.totalSeconds / 3600.0
        val solarNoonMinutes = 720.0 - (4.0 * longitude) - equationOfTime + (zoneOffsetHours * 60.0)

        val sunriseMinutes = solarNoonMinutes - solarHourAngleMinutes(latitude, solarDeclination, 90.833)
        val sunsetMinutes = solarNoonMinutes + solarHourAngleMinutes(latitude, solarDeclination, 90.833)
        val fajrMinutes = solarNoonMinutes - solarHourAngleMinutes(latitude, solarDeclination, 90.0 + fajrAngle)
        val ishaMinutes = solarNoonMinutes + solarHourAngleMinutes(latitude, solarDeclination, 90.0 + ishaAngle)
        val asrAltitude = Math.toDegrees(
            atan(1.0 / (1.0 + tan(abs(Math.toRadians(latitude) - solarDeclination)))),
        )
        val asrMinutes = solarNoonMinutes + solarHourAngleMinutes(latitude, solarDeclination, 90.0 - asrAltitude)

        return PrayerSchedule(
            date = date,
            fajr = minutesToTime(fajrMinutes),
            sunrise = minutesToTime(sunriseMinutes),
            dhuhr = minutesToTime(solarNoonMinutes),
            asr = minutesToTime(asrMinutes),
            maghrib = minutesToTime(sunsetMinutes),
            isha = minutesToTime(ishaMinutes),
        )
    }

    private fun solarHourAngleMinutes(
        latitude: Double,
        declination: Double,
        zenithDegrees: Double,
    ): Double {
        val latitudeRad = Math.toRadians(latitude)
        val zenithRad = Math.toRadians(zenithDegrees)

        val cosHourAngle = (
            cos(zenithRad) - (sin(latitudeRad) * sin(declination))
            ) / (cos(latitudeRad) * cos(declination))

        val bounded = cosHourAngle.coerceIn(-1.0, 1.0)
        val hourAngleDegrees = Math.toDegrees(acos(bounded))
        return hourAngleDegrees * 4.0
    }

    private fun minutesToTime(totalMinutes: Double): LocalTime {
        val safeMinutes = ((totalMinutes % 1440.0) + 1440.0) % 1440.0
        val rounded = safeMinutes.toLong()
        val hour = (rounded / 60).toInt()
        val minute = (rounded % 60).toInt()
        return LocalTime.of(hour, minute)
    }
}
