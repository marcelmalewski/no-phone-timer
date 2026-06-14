package com.marcelmalewski.nophonetimer

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class SessionPart(
    val dayKey: String,
    val durationMs: Long
)

object SessionSplitter {
    fun split(startTime: Long, endTime: Long): List<SessionPart> {
        require(endTime >= startTime)

        val start = Calendar.getInstance().apply {
            timeInMillis = startTime
        }
        val end = Calendar.getInstance().apply {
            timeInMillis = endTime
        }
        val sameDay = start.get(Calendar.YEAR) == end.get(Calendar.YEAR) &&
                    start.get(Calendar.DAY_OF_YEAR) == end.get(Calendar.DAY_OF_YEAR)

        if (sameDay) {
            return listOf(
                SessionPart(dayKey = dateKey(Date(startTime)), durationMs = endTime - startTime)
            )
        }

        val midnight = Calendar.getInstance().apply {
            timeInMillis = startTime

            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            add(Calendar.DAY_OF_YEAR, 1)
        }

        val firstPart = midnight.timeInMillis - startTime
        val secondPart = endTime - midnight.timeInMillis
        return listOf(
            SessionPart(
                dayKey = dateKey(Date(startTime)),
                durationMs = firstPart
            ),
            SessionPart(
                dayKey = dateKey(Date(endTime)),
                durationMs = secondPart
            )
        )
    }

    private fun dateKey(date: Date): String {
        return SimpleDateFormat("yyyy_MM_dd", Locale.getDefault())
            .format(date)
    }
}