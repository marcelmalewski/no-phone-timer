package com.marcelmalewski.nophonetimer

import android.content.Context
import android.os.PowerManager

fun isBatteryOptimizationDisabled(context: Context): Boolean {
    val powerManager = context.getSystemService(PowerManager::class.java)
    return powerManager.isIgnoringBatteryOptimizations(
        context.packageName
    )
}