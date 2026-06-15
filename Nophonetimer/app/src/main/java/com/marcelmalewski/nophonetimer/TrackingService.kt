package com.marcelmalewski.nophonetimer

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder

class TrackingService : Service() {
    private lateinit var receiver: ScreenReceiver

    override fun onCreate() {
        super.onCreate()

        receiver = ScreenReceiver()
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_USER_PRESENT)
        }
        registerReceiver(receiver, filter)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}