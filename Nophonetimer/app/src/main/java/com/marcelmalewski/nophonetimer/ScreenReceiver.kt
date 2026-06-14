package com.marcelmalewski.nophonetimer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class ScreenReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context, intent: Intent
    ) {

        val prefs = context.getSharedPreferences(
            "no_phone_timer", Context.MODE_PRIVATE
        )

        when (intent.action) {

            Intent.ACTION_SCREEN_OFF -> {

                prefs.edit().putLong(
                        "lock_time", System.currentTimeMillis()
                    ).putBoolean(
                        "is_locked", true
                    ).apply()

                Log.d(
                    "NoPhoneTimer", "SCREEN OFF"
                )
            }

            Intent.ACTION_USER_PRESENT -> {
                val lockTime = prefs.getLong("lock_time", 0)

                if (lockTime > 0) {
                    val elapsed = System.currentTimeMillis() - lockTime

                    StatsRepository.addSession(context, lockTime, System.currentTimeMillis())

                    prefs.edit()
                        .putLong("lock_time", 0)
                        .putBoolean("is_locked", false)
                        .apply()

                    Log.d("NoPhoneTimer", "Added ${elapsed / 1000}s")
                }
            }
        }
    }
}