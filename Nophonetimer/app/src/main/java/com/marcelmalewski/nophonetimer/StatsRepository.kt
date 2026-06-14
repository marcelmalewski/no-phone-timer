package com.marcelmalewski.nophonetimer

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class DayStatistics(
    val name: String, val noPhoneDuration: Long
)

object StatsRepository {
    private const val PREFS = "no_phone_timer"
    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state

    private fun dateKey(date: Date): String {
        return "stats_" +
                SimpleDateFormat("yyyy_MM_dd", Locale.getDefault()).format(date)
    }

    private fun todayKey(): String {
        return dateKey(Date())
    }

    fun initialize(context: Context) {
        refresh(context)
    }

    fun addSession(context: Context, startTime: Long, endTime: Long) {
        val parts = SessionSplitter.split(startTime, endTime)
        parts.forEach { part ->
            addToDay(context, part.dayKey, part.duration)
        }

        refresh(context)
    }

    private fun addToDay(context: Context, key: String, durationMs: Long) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val current = prefs.getLong(key, 0)

        prefs.edit()
            .putLong(key, current + durationMs)
            .apply()
    }

    private fun getToday(context: Context): Long {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return prefs.getLong(todayKey(), 0)
    }

    private fun getLast7Days(context: Context): List<DayStatistics> {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val result = mutableListOf<DayStatistics>()
        val calendar = Calendar.getInstance()

        repeat(7) {
            val date = calendar.time
            val value = prefs.getLong(dateKey(date), 0)
            val dayName = SimpleDateFormat("EEE", Locale.getDefault()).format(date)

            result.add(DayStatistics(dayName, value))
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }

        return result
    }

    fun refresh(context: Context) {
        _state.value = AppState(
            todayTotal = getToday(context),
            history = getLast7Days(context)
        )
    }
}