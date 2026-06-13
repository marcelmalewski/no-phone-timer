package com.marcelmalewski.nophonetimer

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class DayStat(
    val dayName: String, val durationMs: Long
)

object StatsRepository {

    private const val PREFS = "no_phone_timer"

    private fun dateKey(date: Date): String {
        return "stats_" + SimpleDateFormat(
            "yyyy_MM_dd", Locale.getDefault()
        ).format(date)
    }

    private fun todayKey(): String {
        return dateKey(Date())
    }

    fun addSession(
        context: Context, durationMs: Long
    ) {

        val prefs = context.getSharedPreferences(
            PREFS, Context.MODE_PRIVATE
        )

        val key = todayKey()

        val current = prefs.getLong(
            key, 0
        )

        prefs.edit().putLong(
                key, current + durationMs
            ).apply()
    }

    fun getToday(
        context: Context
    ): Long {

        val prefs = context.getSharedPreferences(
            PREFS, Context.MODE_PRIVATE
        )

        return prefs.getLong(
            todayKey(), 0
        )
    }

    fun getLast7Days(
        context: Context
    ): List<DayStat> {

        val prefs = context.getSharedPreferences(
            PREFS, Context.MODE_PRIVATE
        )

        val result = mutableListOf<DayStat>()

        val calendar = Calendar.getInstance()

        repeat(7) {

            val date = calendar.time

            val key = dateKey(date)

            val value = prefs.getLong(
                key, 0
            )

            val dayName = SimpleDateFormat(
                "EEE", Locale.getDefault()
            ).format(date)

            result.add(
                DayStat(
                    dayName, value
                )
            )

            calendar.add(
                Calendar.DAY_OF_YEAR, -1
            )
        }

        return result
    }
}