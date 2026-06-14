package com.marcelmalewski.nophonetimer

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class SessionSplitterTest {
    @Test
    fun sameDaySession_isNotSplit() {
        val start = Calendar.getInstance().apply {
            set(2025, Calendar.JUNE, 1, 10, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val end = Calendar.getInstance().apply {
            set(2025, Calendar.JUNE, 1, 10, 30, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val result = SessionSplitter.split(start.timeInMillis, end.timeInMillis)

        assertEquals(1, result.size)
        assertEquals(30 * 60 * 1000L, result[0].duration)
    }

    @Test
    fun sessionCrossingMidnight_isSplit() {
        val start = Calendar.getInstance().apply {
            set(2025, Calendar.JUNE, 1, 23, 50, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val end = Calendar.getInstance().apply {
            set(2025, Calendar.JUNE, 2, 0, 10, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val result = SessionSplitter.split(start.timeInMillis, end.timeInMillis)

        assertEquals(2, result.size)
        assertEquals(10 * 60 * 1000L, result[0].duration)
        assertEquals(10 * 60 * 1000L, result[1].duration)
    }
}