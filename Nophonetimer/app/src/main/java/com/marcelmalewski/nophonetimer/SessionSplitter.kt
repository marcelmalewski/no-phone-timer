package com.marcelmalewski.nophonetimer

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class SessionPart(
    val date: String,
    val duration: Long
)

object SessionSplitter {
    fun split(startTime: Long, endTime: Long): List<SessionPart> {
        require(endTime >= startTime)

        val startDate = Calendar.getInstance().apply {
            timeInMillis = startTime
        }
        val endDate = Calendar.getInstance().apply {
            timeInMillis = endTime
        }
        val isSameDay = startDate.get(Calendar.YEAR) == endDate.get(Calendar.YEAR) &&
                startDate.get(Calendar.DAY_OF_YEAR) == endDate.get(Calendar.DAY_OF_YEAR)
        if (isSameDay) {
            return listOf(
                SessionPart(date = prepareSessionDate(Date(startTime)), duration = endTime - startTime)
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
            SessionPart(date = prepareSessionDate(Date(startTime)), duration = part1),
            SessionPart(date = prepareSessionDate(Date(endTime)), duration = part2)
        )
    }

    private fun prepareSessionDate(date: Date): String {
        return SimpleDateFormat("yyyy_MM_dd", Locale.getDefault())
            .format(date)
    }
}