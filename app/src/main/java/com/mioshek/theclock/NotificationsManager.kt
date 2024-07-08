package com.mioshek.theclock

import android.Manifest
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationsManager(
    private val application: Application,
    private val CHANNEL_CODE: String,
    private val importance: Int,
    private val name: String,
    private val descriptionText: String,
) {
    init {
        createNotificationChannel(importance, name, descriptionText, CHANNEL_CODE)
    }

    fun createNotification(NOTIFICATION_ID: Int, smallIcon: Int, title: String, content: String) {

        val intent = Intent(application, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(application, 0, intent,
            PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(application, CHANNEL_CODE)
            .setSmallIcon(smallIcon)
            .setContentTitle(title)
            .setContentText(content)
//            .setStyle(
//                NotificationCompat.BigTextStyle()
//                    .bigText("long notification content")
//            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(application)) {
            if (ActivityCompat.checkSelfPermission(
                    application.applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    private fun createNotificationChannel(importance: Int, name: String, descriptionText: String, channelCode: String) {

        //define your own channel code here i used a predefined constant
        val channel = NotificationChannel(channelCode, name, importance).apply {
            description = descriptionText
        }

        // Register the channel with the system
        // I am using application class's context here
        val notificationManager: NotificationManager =
            application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}