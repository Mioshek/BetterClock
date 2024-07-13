package com.mioshek.theclock.data

import android.util.Log
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.math.min


enum class TimingState{
    RUNNING,
    OFF,
    PAUSED,
    RINGING
}


data class ClockTime(
    val milliseconds: Long = 0,
    val seconds: Long = 0,
    val minutes: Long = 0,
    val hours: Long = 0,
) {
    private fun toTotalMilliseconds(): Long {
        return milliseconds +
                seconds * 1000 +
                minutes * 60 * 1000 +
                hours * 60 * 60 * 1000
    }

    // Subtraction method
    operator fun minus(other: ClockTime): ClockTime {
        val resultInMilliseconds = this.toTotalMilliseconds() - other.toTotalMilliseconds()

        // Handle negative result (assuming return zero for negative results)
        val nonNegativeResult = if (resultInMilliseconds < 0) 0 else resultInMilliseconds

        // Convert the result back to ClockTime
        val resultHours = nonNegativeResult / (60 * 60 * 1000)
        val resultMinutes = (nonNegativeResult % (60 * 60 * 1000)) / (60 * 1000)
        val resultSeconds = (nonNegativeResult % (60 * 1000)) / 1000
        val resultMilliseconds = nonNegativeResult % 1000

        return ClockTime(
            milliseconds = resultMilliseconds,
            seconds = resultSeconds,
            minutes = resultMinutes,
            hours = resultHours
        )
    }
}

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

        fun calculateTimeLeft(timeInMinutes: Int, daysOfWeek: Array<Boolean>): String {
            val now = LocalDateTime.now()
            val currentDayOfWeek = now.dayOfWeek.value
            val alarmTime = now.withHour(timeInMinutes / 60).withMinute(timeInMinutes % 60)
            var minimumMinutes = Int.MAX_VALUE

            if (daysOfWeek.any { it }) {
                daysOfWeek.forEachIndexed { index, day ->
                    if (day) {
                        val additionalDays = if (currentDayOfWeek < index + 1) index + 1 - currentDayOfWeek else 7 - (currentDayOfWeek - index - 1)
                        val dayToCalculate = alarmTime.plusDays(additionalDays.toLong())
                        val minutes = ChronoUnit.MINUTES.between(now, dayToCalculate).toInt()
                        if (minimumMinutes > minutes) minimumMinutes = minutes
                    }
                }
                val days = minimumMinutes / (24 * 60)
                val hours = (minimumMinutes % (24 * 60)) / 60
                val minutes = minimumMinutes % 60
                return if (days in 1..6) "$days days $hours hours and $minutes minutes"
                else if (hours > 0) "$hours hours and $minutes minutes"
                else "$minutes minutes"
            } else {
                val minutesUntilAlarm = if (now.isBefore(alarmTime)) ChronoUnit.MINUTES.between(now, alarmTime) else ChronoUnit.MINUTES.between(now, alarmTime.plusDays(1))
                val hours = (minutesUntilAlarm / 60).toInt()
                val minutes = (minutesUntilAlarm % 60).toInt()
                return "$hours hours and $minutes minutes"
            }
        }
    }
}