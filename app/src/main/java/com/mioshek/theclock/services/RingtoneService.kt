package com.mioshek.theclock.services

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Handler
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.mioshek.theclock.R
import com.mioshek.theclock.extensions.permissions.RuntimePermissions
import com.mioshek.theclock.receivers.ClockReceiver

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
            this.application,
            CHANNEL_CODE = "Timer",
            importance = NotificationManager.IMPORTANCE_HIGH,
            name = "TimerNotifications",
            descriptionText = ""
        )

        stopHandler = Handler()
        stopRunnable = Runnable {
            stopRingtone()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        wakeUpScreen(this)
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

    private fun wakeUpScreen(context: Context) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE,
            "App:WakeLock"
        )

        wakeLock.acquire(3000) // Acquire wakelock for 3 seconds

        // Show your notification here
        showNotification()

        // Release the wakelock
        wakeLock.release()
    }

    private fun showNotification(){
        notificationManager.showLockScreenNotification(
            R.drawable.hourglass,
            "Timer",
            "Time's Up!",
            NotificationCompat.PRIORITY_HIGH,
            1,
            arrayOf(Triple(R.drawable.stop, "Stop", ClockReceiver::class.java))
        )
        startForeground(1, notificationManager.builder.notification)

        if (ActivityCompat.checkSelfPermission(
                application.applicationContext,
                RuntimePermissions.NOTIFICATIONS.permission
            ) == PackageManager.PERMISSION_GRANTED
        ) ringtone.play()
        // Schedule the stop operation after 20 seconds (20000 milliseconds) for testing
        stopHandler.postDelayed(stopRunnable, 20000)
    }

    private fun stopRingtone() {
        if (ringtone.isPlaying) {
            ringtone.stop()
        }
        stopSelf()
    }
}