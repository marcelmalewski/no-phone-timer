package com.marcelmalewski.nophonetimer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.edit

class ScreenReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val sharedPrefs = context.getSharedPreferences(
            SHARED_PREFS_NAME, Context.MODE_PRIVATE
        )

        when (intent.action) {
            Intent.ACTION_SCREEN_OFF -> handleScreenOff(sharedPrefs)
            Intent.ACTION_USER_PRESENT -> handleUserPresent(context, sharedPrefs)
        }
    }

    private fun handleScreenOff(sharedPrefs: android.content.SharedPreferences) {
        sharedPrefs.edit {
            putLong(LOCK_TIME, System.currentTimeMillis())
        }
        Log.d(TAG, "SCREEN OFF")
    }

    private fun handleUserPresent(context: Context, sharedPrefs: android.content.SharedPreferences) {
        val lockTime = sharedPrefs.getLong(LOCK_TIME, 0)
        if (lockTime <= 0) {
            return
        }

        val unlockTime = System.currentTimeMillis()
        val noPhoneDuration = unlockTime - lockTime

        StatisticsRepository.addSession(
            context, lockTime, unlockTime
        )
        sharedPrefs.edit {
            putLong(LOCK_TIME, 0)
        }
        Log.d(TAG, "Added ${noPhoneDuration / 1000}s")
    }

    private companion object {
        const val TAG = "NoPhoneTimer"
        const val SHARED_PREFS_NAME = "no_phone_timer"
        const val LOCK_TIME = "lock_time"
    }
}