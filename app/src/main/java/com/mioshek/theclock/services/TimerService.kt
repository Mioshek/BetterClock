package com.mioshek.theclock.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.mioshek.theclock.R
import com.mioshek.theclock.data.Storage

class TimerService : Service() {
    private lateinit var wakeLock: PowerManager.WakeLock
//    companion object {
//        const val CHANNEL_ID = "TimerServiceChannel"
//        const val NOTIFICATION_ID = 1
//    }

    override fun onCreate() {
        super.onCreate()
//        createNotificationChannel()
//        val notification = createNotification()
//        startForeground(NOTIFICATION_ID, notification)
        this.startService(Intent(this, RingtoneService::class.java))
        acquireWakeLock()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Handle timer logic here
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        wakeLock.release()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun acquireWakeLock() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TimerApp::WakeLock")
        wakeLock.acquire(10*60*1000L /*10 minutes*/)
    }

//    private fun createNotificationChannel() {
//        val serviceChannel = NotificationChannel(
//            CHANNEL_ID,
//            "Timer Service Channel",
//            NotificationManager.IMPORTANCE_DEFAULT
//        )
//        val manager = getSystemService(NotificationManager::class.java)
//        manager.createNotificationChannel(serviceChannel)
//    }

//    private fun createNotification(): Notification {
//        Log.d("TimerIsRunning","")
//        return NotificationCompat.Builder(this, CHANNEL_ID)
//            .setContentTitle("Timer Running")
//            .setContentText("Your timer is active.")
//            .setSmallIcon(R.drawable.hourglass)
//            .build()
//    }
}