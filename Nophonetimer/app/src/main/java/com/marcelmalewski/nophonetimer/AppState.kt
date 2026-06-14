package com.marcelmalewski.nophonetimer

data class AppState(
    val todayTotal: Long = 0,
    val history: List<DayStatistics> = emptyList()
)