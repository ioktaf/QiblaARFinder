package com.qiblaarfinder.domain.util

import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class PrayerTimesCalculatorTest {
    private val calculator = PrayerTimesCalculator()

    @Test
    fun calculate_returnsChronologicalPrayerTimes() {
        val schedule = calculator.calculate(
            latitude = -6.2088,
            longitude = 106.8456,
            date = LocalDate.of(2026, 4, 17),
            zoneId = ZoneId.of("Asia/Jakarta"),
        )

        assertTrue(schedule.fajr.isBefore(schedule.sunrise))
        assertTrue(schedule.sunrise.isBefore(schedule.dhuhr))
        assertTrue(schedule.dhuhr.isBefore(schedule.asr))
        assertTrue(schedule.asr.isBefore(schedule.maghrib))
        assertTrue(schedule.maghrib.isBefore(schedule.isha))
    }
}

