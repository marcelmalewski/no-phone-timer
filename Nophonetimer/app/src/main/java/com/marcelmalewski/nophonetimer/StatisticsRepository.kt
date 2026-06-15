package com.marcelmalewski.nophonetimer

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.core.content.edit

data class DayStatistics(
    val dayOfWeek: String, val noPhoneDuration: Long
)

object StatisticsRepository {
    private const val SHARED_PREFS_NAME = "no_phone_timer"
    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state

    fun initialize(context: Context) {
        refreshAppState(context)
    }

    fun addSession(context: Context, startTime: Long, endTime: Long) {
        val parts = SessionSplitter.split(startTime, endTime)
        parts.forEach { part -> updateStatistics(context, part.date, part.duration) }
        refreshAppState(context)
    }

    private fun updateStatistics(context: Context, sessionDate: String, duration: Long) {
        val sharedPrefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        val currValue = sharedPrefs.getLong(toSessionDateKey(sessionDate), 0)

        sharedPrefs.edit {
            putLong(toSessionDateKey(sessionDate), currValue + duration)
        }
    }

    private fun refreshAppState(context: Context) {
        _state.value = AppState(
            todayTotal = getTodayTotal(context),
            history = getLast7Days(context)
        )
    }

    private fun getTodayTotal(context: Context): Long {
        val sharedPrefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPrefs.getLong(prepareSessionDateKey(Date()), 0)
    }

    private fun getLast7Days(context: Context): List<DayStatistics> {
        val sharedPrefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        val last7Days = mutableListOf<DayStatistics>()
        val calendar = Calendar.getInstance()

        repeat(7) {
            val currentDate = calendar.time
            val noPhoneDuration = sharedPrefs.getLong(prepareSessionDateKey(currentDate), 0)
            val dayOfWeek = SimpleDateFormat("EEE", Locale.getDefault()).format(currentDate)

            last7Days.add(DayStatistics(dayOfWeek, noPhoneDuration))
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }

        return last7Days
    }

    private fun prepareSessionDateKey(date: Date): String {
        return "stats_" +
                SimpleDateFormat("yyyy_MM_dd", Locale.ROOT).format(date)
    }

    private fun toSessionDateKey(sessionDate: String): String {
        return "stats_$sessionDate"
    }
}