package com.marcelmalewski.nophonetimer

data class DayStatistics(
    val dayOfWeek: String,
    val noPhoneDuration: Long
)

data class AppState(
    val todayTotal: Long = 0,
    val history: List<DayStatistics> = emptyList()
)