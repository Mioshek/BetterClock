package com.mioshek.theclock.services

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.*
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
    private lateinit var wakeLock: PowerManager.WakeLock

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

        stopHandler = Handler(Looper.getMainLooper())
        stopRunnable = Runnable {
            stopRingtone()
        }

        // Acquire wake lock when service is created
        wakeUpScreen(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Show notification and play ringtone
        showNotification()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release any resources
        stopHandler.removeCallbacks(stopRunnable)
        releaseWakeLock()

        if (ringtone.isPlaying) {
            ringtone.stop()
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun wakeUpScreen(context: Context) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE,
            "Ringtone:WakeLock"
        )

        wakeLock.acquire(7000) // Acquire wakelock for 7 seconds
    }

    private fun releaseWakeLock() {
        if (wakeLock.isHeld) {
            wakeLock.release()
        }
    }

    private fun showNotification() {
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
        ) {
            ringtone.play()
        }

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
