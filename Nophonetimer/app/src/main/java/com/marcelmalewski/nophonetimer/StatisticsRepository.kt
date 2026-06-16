package com.marcelmalewski.nophonetimer

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object StatisticsRepository {

    private const val SCREEN_INTERACTIVE = 15
    private const val SCREEN_NON_INTERACTIVE = 16

    private val _state = MutableStateFlow(AppState())

    val state: StateFlow<AppState> = _state

    fun refresh(context: Context) {
        _state.value = AppState(
            todayTotal = getTodayNoPhoneTime(context), history = getLast7Days(context)
        )
    }

    private fun getTodayNoPhoneTime(
        context: Context
    ): Long {

        val now = System.currentTimeMillis()

        val calendar = Calendar.getInstance()

        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val startOfDay = calendar.timeInMillis

        val interactiveTime = getInteractiveTime(
            context, startOfDay, now
        )

        val elapsedToday = now - startOfDay

        return elapsedToday - interactiveTime
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

            val end = if (offset == 0) {
                System.currentTimeMillis()
            } else {
                start + 24 * 60 * 60 * 1000L
            }

            val interactiveTime = getInteractiveTime(
                context, start, end
            )

            val totalPeriod = end - start

            val noPhoneTime = totalPeriod - interactiveTime

            result.add(
                DayStatistics(
                    dayOfWeek = SimpleDateFormat(
                        "EEE", Locale.getDefault()
                    ).format(start), noPhoneDuration = noPhoneTime
                )
            )
        }

        return result
    }

    private fun getInteractiveTime(
        context: Context, start: Long, end: Long
    ): Long {

        val usageStatsManager = context.getSystemService(
            UsageStatsManager::class.java
        )

        val events = usageStatsManager.queryEvents(
            start, end
        )

        val event = UsageEvents.Event()

        var interactiveStart: Long? = null
        var total = 0L

        while (events.hasNextEvent()) {

            events.getNextEvent(event)

            when (event.eventType) {

                SCREEN_INTERACTIVE -> {
                    interactiveStart = event.timeStamp
                }

                SCREEN_NON_INTERACTIVE -> {

                    interactiveStart?.let {
                        total += event.timeStamp - it
                    }

                    interactiveStart = null
                }
            }
        }

        interactiveStart?.let {
            total += end - it
        }

        return total
    }
}