package com.example.runpath

import org.junit.Assert.assertEquals
import org.junit.Test
import com.example.runpath.ui.theme.Maps.FormatTime

class FormatTimeTest {

    @Test
    fun `test FormatTime with normal values`() {
        val time = 125000L // 2 minutes 5 seconds
        val expected = "02:05:000"

        val actual = FormatTime(time)

        assertEquals(expected, actual)
    }

    @Test
    fun `test FormatTime with zero`() {
        val time = 0L
        val expected = "00:00:000"

        val actual = FormatTime(time)

        assertEquals(expected, actual)
    }

    @Test
    fun `test FormatTime with large time value`() {
        val time = 3599000L // 59 minutes 59 seconds
        val expected = "59:59:000"

        val actual = FormatTime(time)

        assertEquals(expected, actual)
    }

}