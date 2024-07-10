package com.mioshek.theclock.services

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Handler
import android.os.IBinder
import com.mioshek.theclock.R

class RingtoneService : Service() {
    private lateinit var ringtone: Ringtone
    private lateinit var notificationManager: NotificationsManager
    private lateinit var stopHandler: Handler
    private lateinit var stopRunnable: Runnable

    override fun onCreate() {
        super.onCreate()
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        ringtone = RingtoneManager.getRingtone(this, uri)
        notificationManager = NotificationsManager(
            application = application,
            CHANNEL_CODE = "Timer",
            importance = NotificationManager.IMPORTANCE_HIGH,
            name = "TimerNotifications",
            descriptionText = ""
        )

        // Initialize Handler and Runnable for stopping the ringtone after 1 minute
        stopHandler = Handler()
        stopRunnable = Runnable {
            stopRingtone()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        notificationManager.createNotification(
            1,
            R.drawable.hourglass,
            "Timer",
            "Time's Up!",
        )
        ringtone.play()
        startForeground(1, notificationManager.builder.notification)

        // Schedule the stop operation after 1 minute (60,000 milliseconds)
        stopHandler.postDelayed(stopRunnable, 20000) // 60 seconds = 60,000 milliseconds

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove any pending callbacks to prevent the Runnable from executing after service is destroyed
        stopHandler.removeCallbacks(stopRunnable)

        if (ringtone.isPlaying) {
            ringtone.stop()
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun stopRingtone() {
        if (ringtone.isPlaying) {
            ringtone.stop()
        }
        // Stop the service after stopping the ringtone
        stopSelf()
    }
}
