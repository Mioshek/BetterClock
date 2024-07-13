package com.mioshek.theclock.receivers

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver: BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        this.onReceive(context, intent)
    }

    companion object{
        fun createActionPendingIntent(context: Context?, action: String): PendingIntent{
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                this.action = action
            }
            return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }
    }
}