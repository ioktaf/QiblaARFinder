package com.qiblaarfinder.domain.util

import org.junit.Assert.assertEquals
import org.junit.Test

class QiblaCalculatorTest {
    @Test
    fun bearingFromJakarta_pointsNorthWestTowardKaaba() {
        val bearing = QiblaCalculator.bearingFrom(
            latitude = -6.2088,
            longitude = 106.8456,
        )

        assertEquals(295.1, bearing, 1.0)
    }
}

