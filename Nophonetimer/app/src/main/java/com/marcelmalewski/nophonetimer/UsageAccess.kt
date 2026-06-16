package com.marcelmalewski.nophonetimer

import android.content.Context
import android.app.usage.UsageStatsManager

fun hasUsageAccess(context: Context): Boolean {
    val usageStatsManager = context.getSystemService(UsageStatsManager::class.java)
    val now = System.currentTimeMillis()

    val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            now - 24 * 60 * 60 * 1000L,
            now
        )
    return stats.isNotEmpty()
}