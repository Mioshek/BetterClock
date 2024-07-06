package com.mioshek.theclock.data

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit


enum class TimingState{
    RUNNING,
    OFF,
    PAUSED,
}


data class ClockTime(
    val milliseconds: Long = 0,
    val seconds: Long = 0,
    val minutes: Long = 0,
    val hours: Long = 0,
)

class TimeFormatter{
    companion object{
        fun getFullClockTime(elapsedTime: Long): ClockTime {
            val hours = (elapsedTime / (1000 * 60 * 60))
            val minutes = (elapsedTime / (1000 * 60)) % 60
            val seconds = (elapsedTime / 1000) % 60
            val milliseconds = elapsedTime % 1000
            return ClockTime(milliseconds, seconds, minutes, hours)
        }

        fun getClockTimeWithoutMillis(elapsedTime: Long): ClockTime {
            val hours = (elapsedTime / (1000 * 60 * 60))
            val minutes = (elapsedTime / (1000 * 60)) % 60
            val seconds = (elapsedTime / 1000) % 60
            return ClockTime(seconds = seconds, minutes = minutes, hours = hours)
        }

        /**
         * @return array of string which includes hours, minutes, AM/PM if as24h is false
         */
        fun format(timeInMinutes: Int, as24h: Boolean): Array<String> {
            val hour = timeInMinutes / 60
            val minute = timeInMinutes % 60
            val formattedMinutes = if (minute > 9) minute else "0$minute"
            if (as24h) {
                if (hour < 10) return arrayOf("  $hour", ":$formattedMinutes")
                return arrayOf("$hour", ":$formattedMinutes")
            }
            if (hour == 0) return arrayOf("12", ":$formattedMinutes","AM")
            if (hour < 10) return arrayOf("  $hour", ":$formattedMinutes", "AM")
            if (hour < 12) return arrayOf("$hour", ":$formattedMinutes","AM")
            return if (hour == 12) arrayOf("12:", "$formattedMinutes}","PM")
            else{
                if ((hour - 12) < 10) return arrayOf((hour - 12).toString(), ":$formattedMinutes", "PM")
                return arrayOf((hour - 12).toString(), ":$formattedMinutes", "PM")
            }
        }

        fun calculateTimeLeft(time: Int, daysOfWeek: Array<Boolean>): String {
            val now = LocalTime.now()
            val currentDay = LocalDate.now()
            val alarmTime = LocalTime.of(time / 60, time % 60)

            return (0..6)
                .map { (currentDay.dayOfWeek.value + it - 1) % 7 }
                .filter { daysOfWeek[it] }
                .map {
                    val nextAlarm = currentDay.plusDays(if (it == 0 && now.isBefore(alarmTime)) 0 else it.toLong()).atTime(alarmTime)
                    ChronoUnit.MINUTES.between(currentDay.atTime(now), nextAlarm)
                }
                .minOrNull()
                ?.let { "${it / 60} hours ${it % 60} minutes left" } ?: "Once"
        }
    }
}