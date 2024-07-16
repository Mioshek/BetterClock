package com.mioshek.theclock.services

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.mioshek.theclock.receivers.AlarmReceiver
import java.util.Calendar
import kotlin.coroutines.coroutineContext


class AlarmService(application: Application) {

    private val alarmMgr = application.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val alarmIntent = Intent(application.applicationContext, AlarmReceiver::class.java)
    private val alarmPendingIntent = PendingIntent.getBroadcast(
        application.applicationContext,
        0,
        alarmIntent,
        PendingIntent.FLAG_IMMUTABLE
    )


    fun setAlarm(context: Context, time: Int, daysOfWeek: Array<Boolean>) {
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val minutes = time % 60
        val hours = time / 60
        var once = true

        daysOfWeek.forEachIndexed { index, b ->
            if (b) {
                val interval: Long = 7 * 24 * 60 * 60 * 1000
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hours)
                    set(Calendar.MINUTE, minutes)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                    set(Calendar.DAY_OF_WEEK, index + 1)
                }
                alarmMgr.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    interval,
                    alarmPendingIntent
                )
                once = false
            }
        }

        if (once) {
            val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hours)
                set(Calendar.MINUTE, minutes)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                set(Calendar.DAY_OF_WEEK, today)
            }

            if (calendar.time.before(Calendar.getInstance().time)){
                calendar.set(Calendar.DAY_OF_WEEK, today + 1)
            }

            alarmMgr.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                alarmPendingIntent
            )
        }
    }

    fun cancelAlarm(context: Context){
        alarmMgr.cancel(alarmPendingIntent)
    }
}