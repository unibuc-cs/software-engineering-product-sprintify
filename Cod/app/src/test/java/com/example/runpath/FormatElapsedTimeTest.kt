package com.example.runpath

import org.junit.Assert.assertEquals
import org.junit.Test
import com.example.runpath.ui.theme.Maps.formatElapsedTime

class FormatElapsedTimeTest {

    @Test
    fun `test formatElapsedTime for normal case`() {
        val millis = 3661000L // 1 hour, 1 minute, 1 second
        val expected = "01:01:01"

        val actual = formatElapsedTime(millis)

        assertEquals(expected, actual)
    }

    @Test
    fun `test formatElapsedTime for zero milliseconds`() {
        val millis = 0L
        val expected = "00:00:00"

        val actual = formatElapsedTime(millis)

        assertEquals(expected, actual)
    }

    @Test
    fun `test formatElapsedTime for just seconds`() {
        val millis = 45000L // 45 seconds
        val expected = "00:00:45"

        val actual = formatElapsedTime(millis)

        assertEquals(expected, actual)
    }

    @Test
    fun `test formatElapsedTime for just minutes`() {
        val millis = 900000L // 15 minutes
        val expected = "00:15:00"

        val actual = formatElapsedTime(millis)

        assertEquals(expected, actual)
    }

    @Test
    fun `test formatElapsedTime for long durations`() {
        val millis = 86400000L // 24 hours (1 day)
        val expected = "24:00:00"

        val actual = formatElapsedTime(millis)

        assertEquals(expected, actual)
    }

}