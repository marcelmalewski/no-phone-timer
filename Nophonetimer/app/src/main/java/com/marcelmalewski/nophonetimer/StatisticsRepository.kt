package com.marcelmalewski.nophonetimer

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object StatisticsRepository {

    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state

    fun refresh(context: Context) {
        _state.value = AppState(
            todayTotal = getTodayTotal(context),
            history = getLast7Days(context)
        )
    }

    private fun getTodayTotal(
        context: Context
    ): Long {

        val calendar = Calendar.getInstance()

        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return getScreenTime(
            context,
            calendar.timeInMillis,
            System.currentTimeMillis()
        )
    }

    private fun getLast7Days(
        context: Context
    ): List<DayStatistics> {

        val result = mutableListOf<DayStatistics>()

        repeat(7) { offset ->

            val cal = Calendar.getInstance()

            cal.add(Calendar.DAY_OF_YEAR, -offset)

            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)

            val start = cal.timeInMillis

            cal.add(Calendar.DAY_OF_YEAR, 1)

            val end = cal.timeInMillis

            val label = SimpleDateFormat(
                "EEE",
                Locale.getDefault()
            ).format(start)

            result.add(
                DayStatistics(
                    dayOfWeek = label,
                    noPhoneDuration = getScreenTime(
                        context,
                        start,
                        end
                    )
                )
            )
        }

        return result
    }

    private fun getScreenTime(
        context: Context,
        start: Long,
        end: Long
    ): Long {

        val usageStatsManager =
            context.getSystemService(
                UsageStatsManager::class.java
            )

        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            start,
            end
        )

        return stats.sumOf(
            UsageStats::getTotalTimeInForeground
        )
    }
}