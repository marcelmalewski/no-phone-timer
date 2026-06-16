package com.marcelmalewski.nophonetimer

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object StatisticsRepository {
    private const val SCREEN_INTERACTIVE = 15
    private const val SCREEN_NON_INTERACTIVE = 16
    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state

    suspend fun refresh(context: Context) {
        val start = System.currentTimeMillis()

        val newState = withContext(Dispatchers.IO) {

            val history = getLast7Days(context)

            AppState(
                todayTotal = history.first().noPhoneDuration,
                history = history
            )
        }

        Log.d(
            "NoPhoneTimer",
            "refresh took ${System.currentTimeMillis() - start} ms"
        )

        _state.value = newState
    }

    private fun getLast7Days(
        context: Context
    ): List<DayStatistics> {

        val usageStatsManager = context.getSystemService(
            UsageStatsManager::class.java
        )

        val now = System.currentTimeMillis()

        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val startOfToday = todayStart.timeInMillis

        val startOfRange = startOfToday - (6 * 24 * 60 * 60 * 1000L)

        val events = usageStatsManager.queryEvents(
            startOfRange,
            now
        )

        val interactivePerDay = LongArray(7)

        val event = UsageEvents.Event()

        var interactiveStart: Long? = null

        while (events.hasNextEvent()) {

            events.getNextEvent(event)

            when (event.eventType) {

                SCREEN_INTERACTIVE -> {
                    interactiveStart = event.timeStamp
                }

                SCREEN_NON_INTERACTIVE -> {

                    interactiveStart?.let { start ->

                        addSessionToBuckets(
                            start = start,
                            end = event.timeStamp,
                            startOfRange = startOfRange,
                            buckets = interactivePerDay
                        )
                    }

                    interactiveStart = null
                }
            }
        }

        interactiveStart?.let { start ->

            addSessionToBuckets(
                start = start,
                end = now,
                startOfRange = startOfRange,
                buckets = interactivePerDay
            )
        }

        return buildList {

            repeat(7) { offset ->

                val dayStart =
                    startOfToday - (offset * 24 * 60 * 60 * 1000L)

                val dayEnd =
                    if (offset == 0) now
                    else dayStart + 24 * 60 * 60 * 1000L

                val totalPeriod = dayEnd - dayStart

                val bucketIndex = 6 - offset

                val noPhoneTime =
                    totalPeriod - interactivePerDay[bucketIndex]

                add(
                    DayStatistics(
                        dayOfWeek = SimpleDateFormat(
                            "EEE",
                            Locale.getDefault()
                        ).format(dayStart),
                        noPhoneDuration = noPhoneTime
                    )
                )
            }
        }
    }

    private fun addSessionToBuckets(
        start: Long,
        end: Long,
        startOfRange: Long,
        buckets: LongArray
    ) {

        var currentStart = start

        while (currentStart < end) {

            val dayIndex =
                ((currentStart - startOfRange) /
                        (24 * 60 * 60 * 1000L)).toInt()

            if (dayIndex !in 0..6) {
                return
            }

            val nextDayBoundary =
                startOfRange +
                        ((dayIndex + 1) * 24 * 60 * 60 * 1000L)

            val segmentEnd =
                minOf(end, nextDayBoundary)

            buckets[dayIndex] +=
                segmentEnd - currentStart

            currentStart = segmentEnd
        }
    }
}