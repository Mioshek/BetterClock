package com.mioshek.theclock.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.mioshek.theclock.data.Storage
import com.mioshek.theclock.services.AlarmService
import com.mioshek.theclock.services.RingtoneService
import com.mioshek.theclock.services.RingtoneType

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Storage.put("RingtoneType", RingtoneType.ALARM)
        context.startService(Intent(context, RingtoneService::class.java))
    }
}
class AlarmStopReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("StoppingAlarm", "Attempting to stop the alarm service")
        val stopIntent = Intent(context, RingtoneService::class.java)
        context.stopService(stopIntent)
    }
}

class SnoozeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val snoozeIntent = Intent(context, RingtoneService::class.java)
        context.stopService(snoozeIntent)  // Stop current service
        context.startService(snoozeIntent.putExtra("snooze", true))  // Restart service with snooze flag
    }
}

class TimerReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        Storage.put("RingtoneType", RingtoneType.TIMER)
        context?.stopService(Intent(context, RingtoneService::class.java))
    }
}
