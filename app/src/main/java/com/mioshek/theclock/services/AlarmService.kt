package com.mioshek.theclock.services

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.mioshek.theclock.receivers.AlarmReceiver
import java.time.LocalDate
import java.util.Calendar

class AlarmService(application: Application) {

    private val alarmMgr = application.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val alarmIntent = Intent(application.applicationContext, AlarmReceiver::class.java).let { intent ->
        PendingIntent.getBroadcast(application.applicationContext, 0, intent,
            PendingIntent.FLAG_IMMUTABLE)
    }

    fun setAlarm(time: Int, daysOfWeek: Array<Boolean>){
        val minutes = time % 60
        val hours = time / 60
        var once = true
        daysOfWeek.forEachIndexed { index, b ->
            if (b){
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hours)
                    set(Calendar.MINUTE, minutes)
                    set(Calendar.DAY_OF_WEEK, index + 1)
                }
                alarmMgr.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    7 * 24 * 60 * 60 * 1000,
                    alarmIntent
                )
                once = false
            }
        }

        if (once){
            val today = LocalDate.now()
            val dayOfWeek = today.dayOfWeek
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hours)
                set(Calendar.MINUTE, minutes)
                set(Calendar.SECOND, 0)
                set(Calendar.DAY_OF_WEEK, dayOfWeek.value + 1)
            }
            alarmMgr.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                alarmIntent
            )
            Log.d("Time", calendar.time.toString())
        }
    }
}