package com.qiblaarfinder.domain.model

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class PrayerSchedule(
    val date: LocalDate,
    val fajr: LocalTime,
    val sunrise: LocalTime,
    val dhuhr: LocalTime,
    val asr: LocalTime,
    val maghrib: LocalTime,
    val isha: LocalTime,
) {
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    fun prayerEntries(): List<Pair<String, LocalTime>> = listOf(
        "Fajr" to fajr,
        "Dhuhr" to dhuhr,
        "Asr" to asr,
        "Maghrib" to maghrib,
        "Isha" to isha,
    )

    fun sunriseLabel(): String = sunrise.format(timeFormatter)

    fun formatted(time: LocalTime): String = time.format(timeFormatter)

    fun nextPrayer(now: LocalTime = LocalTime.now()): Pair<String, LocalTime> {
        return prayerEntries().firstOrNull { now.isBefore(it.second) } ?: ("Fajr Besok" to fajr)
    }
}

