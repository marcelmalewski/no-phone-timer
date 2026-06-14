package com.marcelmalewski.nophonetimer

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class SessionPart(
    val dayKey: String,
    val duration: Long
)

object SessionSplitter {
    fun split(startTime: Long, endTime: Long): List<SessionPart> {
        require(endTime >= startTime)

        val calendarWithStartTime = Calendar.getInstance().apply {
            timeInMillis = startTime
        }
        val calendarWithEndTime = Calendar.getInstance().apply {
            timeInMillis = endTime
        }
        val isSameDay = calendarWithStartTime.get(Calendar.YEAR) == calendarWithEndTime.get(Calendar.YEAR) &&
                calendarWithStartTime.get(Calendar.DAY_OF_YEAR) == calendarWithEndTime.get(Calendar.DAY_OF_YEAR)
        if (isSameDay) {
            return listOf(
                SessionPart(dayKey = prepareDayKey(Date(startTime)), duration = endTime - startTime)
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

        val part1 = midnight.timeInMillis - startTime
        val part2 = endTime - midnight.timeInMillis
        return listOf(
            SessionPart(dayKey = prepareDayKey(Date(startTime)), duration = part1),
            SessionPart(dayKey = prepareDayKey(Date(endTime)), duration = part2)
        )
    }

    private fun prepareDayKey(date: Date): String {
        return SimpleDateFormat("yyyy_MM_dd", Locale.getDefault())
            .format(date)
    }
}