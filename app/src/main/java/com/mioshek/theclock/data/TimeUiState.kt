package com.mioshek.theclock.data

import android.util.Range

enum class TimingState{
    RUNNING,
    OFF,
    PAUSED,
}


data class ClockTime(
    val milliseconds: Long = 0,
    val seconds: Long = 50,
    val minutes: Long = 59,
    val hours: Long = 0,
)

fun getTime(elapsedTime: Long): ClockTime {
    val hours = (elapsedTime / (1000 * 60 * 60)) % 24
    val minutes = (elapsedTime / (1000 * 60)) % 60
    val seconds = (elapsedTime / 1000) % 60
    val milliseconds = elapsedTime % 1000
    return ClockTime(milliseconds, seconds, minutes, hours)
}

/**
 * @param [timePrecision] represents what [time]: [String] will look like e.g 4 displays HH:MM:SS.mmm, 3; HH:MM:SS etc.
 *
 * If [ClockTime.hours] == 0 then function does not display hours.
 */
fun getStringTime(time:ClockTime, timePrecision: Int): String {
    val hours = ensureFormatTime(time.hours,2)
    val minutes = ensureFormatTime(time.minutes,2)
    val seconds = ensureFormatTime(time.seconds,2)
    val milliseconds = ensureFormatTime(time.milliseconds,3)
    val timeArray = arrayOf(hours, minutes, seconds, milliseconds)
    val stringTime = StringBuilder()

    for (i in 0..<timePrecision){
        if (i == 0 && timeArray[i] == "00"){
            continue
        }
        stringTime.append(timeArray[i])
        val separator = if (i == 2 && i != timePrecision - 1) "." else if (i != timePrecision - 1) ":" else ""
        stringTime.append(separator)
    }
    return  stringTime.toString()
}

private fun ensureFormatTime(timeValue: Long, digits: Int): String {
    return "%0${digits}d".format(timeValue)
}
