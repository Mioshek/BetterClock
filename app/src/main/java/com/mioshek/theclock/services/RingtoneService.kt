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
import android.os.Looper
import android.os.PowerManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.mioshek.theclock.R
import com.mioshek.theclock.data.Storage
import com.mioshek.theclock.extensions.permissions.RuntimePermissions
import com.mioshek.theclock.receivers.AlarmStopReceiver
import com.mioshek.theclock.receivers.SnoozeReceiver
import com.mioshek.theclock.receivers.TimerReceiver

enum class RingtoneType(val smallIcon: Int, val title: String, val content: String, val actions: Array<Triple<Int, String, Class<*>>>){
    ALARM(
        R.drawable.alarm,
        "Alarm",
        "",
        arrayOf(
            Triple(R.drawable.stop, "Snooze", SnoozeReceiver::class.java),
            Triple(R.drawable.stop, "Stop Alarm", AlarmStopReceiver::class.java)
        )
    ),
    TIMER(
        R.drawable.hourglass,
        "Timer",
        "Time's Up!",
        arrayOf(Triple(R.drawable.stop, "Stop", TimerReceiver::class.java))
    )
}

class RingtoneService: Service(){
    private var serviceId: Int? = null
    private lateinit var ringtoneType: RingtoneType
    private lateinit var ringtone: Ringtone
    private lateinit var notificationManager: NotificationsManager
    private lateinit var stopHandler: Handler
    private lateinit var stopRunnable: Runnable
    private lateinit var wakeLock: PowerManager.WakeLock

    override fun onCreate() {
        super.onCreate()
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        ringtone = RingtoneManager.getRingtone(this, uri)
        stopHandler = Handler(Looper.getMainLooper())
        stopRunnable = Runnable { stopRingtone() }
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        serviceId = ServiceManager.getRunningServicesCount(applicationContext)
        if (intent?.getBooleanExtra("snooze", false) == true) {
            stopHandler.postDelayed({ startService(Intent(this, RingtoneService::class.java)) }, 10 * 60 * 1000) // Snooze for 10 minutes
            Storage.put("RingtoneType", RingtoneType.ALARM)
        } else {
            ringtoneType = Storage.take("RingtoneType")
            wakeUpScreen()
            showNotification(ringtoneType)
            playRingtone()
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopHandler.removeCallbacks(stopRunnable)
        if (ringtone.isPlaying){
            ringtone.stop()
        }
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(serviceId!!)

    }

    private fun playRingtone() {
        if (ActivityCompat.checkSelfPermission(
                this,
                RuntimePermissions.NOTIFICATIONS.permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            ringtone.play()
            stopHandler.postDelayed(stopRunnable, 20000) // Stop after 20 seconds
        }
    }

    private fun showNotification(ringtoneType: RingtoneType) {
        notificationManager = NotificationsManager(
            application,
            CHANNEL_CODE = "Ringtone:$serviceId",
            importance = NotificationManager.IMPORTANCE_DEFAULT,
            name = "Notification:$serviceId",
            descriptionText = ""
        )

        notificationManager.showLockScreenNotification(
            NOTIFICATION_ID = serviceId!!,
            smallIcon = ringtoneType.smallIcon,
            title = ringtoneType.title,
            content = ringtoneType.content,
            actions = ringtoneType.actions,
            priority = NotificationCompat.PRIORITY_DEFAULT
        )
        startForeground(serviceId!!, notificationManager.builder.notification)
    }

    private fun wakeUpScreen() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE,
            "TheUltimateClock:$serviceId"
        )
        wakeLock.acquire(7000)
    }

    private fun stopRingtone(){
        if (ringtone.isPlaying) {
            ringtone.stop()
        }
        stopSelf()
    }
}
