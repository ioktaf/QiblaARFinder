package com.qiblaarfinder.domain.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LocationInputValidatorTest {
    @Test
    fun validate_rejectsLatitudeOutsideValidRange() {
        val message = LocationInputValidator.validate(
            latitude = 91.0,
            longitude = 106.8456,
        )

        assertEquals("Latitude harus berada di antara -90 sampai 90.", message)
    }

    @Test
    fun validate_rejectsLongitudeOutsideValidRange() {
        val message = LocationInputValidator.validate(
            latitude = -6.2088,
            longitude = 181.0,
        )

        assertEquals("Longitude harus berada di antara -180 sampai 180.", message)
    }

    @Test
    fun validate_acceptsCoordinateWithinRange() {
        val message = LocationInputValidator.validate(
            latitude = -6.2088,
            longitude = 106.8456,
        )

        assertNull(message)
    }
}
