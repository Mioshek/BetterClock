package com.mioshek.theclock.data


enum class TimingState{
    RUNNING,
    OFF,
    PAUSED,
}


data class ClockTime(
    val milliseconds: Long = 0,
    val seconds: Long = 10,
    val minutes: Long = 0,
    val hours: Long = 0,
)

fun getTime(elapsedTime: Long): ClockTime {
    val hours = (elapsedTime / (1000 * 60 * 60)) % 24
    val minutes = (elapsedTime / (1000 * 60)) % 60
    val seconds = (elapsedTime / 1000) % 60
    val milliseconds = elapsedTime % 1000
    return ClockTime(milliseconds, seconds, minutes, hours)
}

private val digits = intArrayOf(2, 2, 2, 3)
private val separators = charArrayOf(' ', ':', ':', '.')

/**
 * @param clock time in format like in [ClockTime]
 * @param start index of returned time.
 * @param end index of returned time.
 *
 * [start] = 0, [end] = 4 => HH:MM:SS.mmm
 *
 * [start] = 0, [end] = 3 => HH:MM:SS
 *
 * [start] = 1, [end] = 4 => MM:SS.mmm etc.
 *
 * If [ClockTime.hours] == 0 then function does not display hours.
 */
fun getStringTime(clock: ClockTime, start: Int, end: Int): String {
    val numbers = intArrayOf(clock.hours.toInt(), clock.minutes.toInt(), clock.seconds.toInt(), clock.milliseconds.toInt())
    val time = StringBuilder()
    var i: Int = if (numbers[0] > 0) start else start + 1
    padValue(time, numbers[i], digits[i])
    i++
    while (i < end) {
        time.append(separators[i])
        padValue(time, numbers[i], digits[i])
        i++
    }
    return time.toString()
}

fun padValue(str: StringBuilder, value: Int, digits: Int) {
    var digits = digits
    val strValue = value.toString()
    digits -= strValue.length
    while (digits > 0) {
        str.append('0')
        digits--
    }
    str.append(strValue)
}