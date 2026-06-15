package com.marcelmalewski.nophonetimer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_LOCKED_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED -> {

                Log.i(
                    "BootReceiver",
                    "Received action=${intent.action}"
                )
                context.startService(
                    Intent(context, TrackingService::class.java)
                )
            }
        }
    }
}