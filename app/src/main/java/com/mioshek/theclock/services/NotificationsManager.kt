package com.mioshek.theclock.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.mioshek.theclock.MainActivity
import com.mioshek.theclock.R
import com.mioshek.theclock.data.Storage
import com.mioshek.theclock.extensions.permissions.PermissionManager.Companion.checkPermission
import com.mioshek.theclock.extensions.permissions.RuntimePermissions
import com.mioshek.theclock.receivers.ClockReceiver

class NotificationsManager(
    private val application: Application,
    private val CHANNEL_CODE: String,
    importance: Int,
    name: String,
    descriptionText: String,
) {
    lateinit var builder: NotificationCompat.Builder

    init {createNotificationChannel(importance, name, descriptionText, CHANNEL_CODE) }

    @SuppressLint("MissingPermission", "NewApi")
    fun createNotification(
        NOTIFICATION_ID: Int,
        smallIcon: Int,
        title: String,
        content: String,
    ) {
        val intent = Intent(application, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            application,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(application.applicationContext, ClockReceiver::class.java)
        val stopPendingIntent = PendingIntent.getBroadcast(
            application.applicationContext,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        builder = NotificationCompat.Builder(application, CHANNEL_CODE)
            .setSmallIcon(smallIcon)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(content)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(R.drawable.stop, "Dismiss", stopPendingIntent)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(application)) {
            if (checkPermission(application.applicationContext, RuntimePermissions.NOTIFICATIONS.permission)) {
                requestNotificationPermission()
                return
            }
            checkNotificationSettings()
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    private fun createNotificationChannel(importance: Int, name: String, descriptionText: String, channelCode: String) {
        val channel = NotificationChannel(channelCode, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun checkNotificationSettings() {
        if (!NotificationManagerCompat.from(application.applicationContext).areNotificationsEnabled()) {
            Toast.makeText(application.applicationContext, "Please enable notifications in settings", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra(Settings.EXTRA_APP_PACKAGE, application.packageName)
            }
            ContextCompat.startActivity(application.applicationContext, intent, null)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission() {
        val activity = Storage.take<Activity>("Activity")
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            1
        )
    }
}
