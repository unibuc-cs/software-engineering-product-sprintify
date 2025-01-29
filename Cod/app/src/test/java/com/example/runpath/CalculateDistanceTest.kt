package com.example.runpath

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.roundToInt
//import com.example.runpath.ui.theme.Maps.calculateDistance

// Custom LatLng data class for testing - avoids the need for google maps SDK
data class LatLng(val latitude: Double, val longitude: Double)

// Copy of the function for testing (only in tests, avoid modifying main code)
fun calculateDistance(point1: LatLng, point2: LatLng): Double {
    val earthRadius = 6371.0 // Radius of the Earth in kilometers
    val latDiff = Math.toRadians(point2.latitude - point1.latitude)
    val lngDiff = Math.toRadians(point2.longitude - point1.longitude)
    val a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
            Math.cos(Math.toRadians(point1.latitude)) * Math.cos(Math.toRadians(point2.latitude)) *
            Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    return earthRadius * c
}

class CalculateDistanceTest {

    @Test
    fun `test calculateDistance between two known points`() {
        val point1 = LatLng(52.5200, 13.4050) // Berlin, Germany
        val point2 = LatLng(48.8566, 2.3522)  // Paris, France

        val expectedDistance = 877.3 // Approximate real distance in km
        val actualDistance = calculateDistance(point1, point2)

        assertEquals(expectedDistance.roundToInt(), actualDistance.roundToInt()) // Rounding to ignore minor precision differences
    }

    @Test
    fun `test calculateDistance with same point returns zero`() {
        val point = LatLng(40.7128, -74.0060) // New York City

        val actualDistance = calculateDistance(point, point)

        assertEquals(0.0, actualDistance, 0.0001) // Distance should be exactly zero
    }

    @Test
    fun `test calculateDistance between two very close points`() {
        val point1 = LatLng(51.5074, -0.1278) // London
        val point2 = LatLng(51.5075, -0.1277) // Very close to London

        val actualDistance = calculateDistance(point1, point2)

        assert(actualDistance < 0.02) // Should be a very small number (less than 20 meters)
    }

}