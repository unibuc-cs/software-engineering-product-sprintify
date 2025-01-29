package com.example.runpath

import org.junit.Assert.assertEquals
import org.junit.Test
import com.example.runpath.ui.theme.Maps.calculatePace

class CalculatePaceTest {

    @Test
    fun `test calculatePace with normal values`() {
        val timeMillis = 300000L // 5 minutes (300 sec)
        val distanceKm = 1.0 // 1 km
        val expectedPace = "5'00''" // 5 minutes per km

        val actualPace = calculatePace(timeMillis, distanceKm)

        assertEquals(expectedPace, actualPace)
    }

    @Test
    fun `test calculatePace with zero distance`() {
        val timeMillis = 300000L // 5 minutes
        val distanceKm = 0.0 // Distance is 0

        val expectedPace = "MAX'--''" // Should return "maximum" pace

        val actualPace = calculatePace(timeMillis, distanceKm)

        assertEquals(expectedPace, actualPace)
    }

    @Test
    fun `test calculatePace with a small distance`() {
        val timeMillis = 60000L // 1 minute
        val distanceKm = 0.2 // 200 meters
        val expectedPace = "5'00''" // 5 minutes per km

        val actualPace = calculatePace(timeMillis, distanceKm)

        assertEquals(expectedPace, actualPace)
    }
}