package com.marcelmalewski.nophonetimer

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object StatsRepository {

    private const val PREFS = "no_phone_timer"

    private fun todayKey(): String {
        return "stats_" + SimpleDateFormat(
            "yyyy_MM_dd", Locale.getDefault()
        ).format(Date())
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
}