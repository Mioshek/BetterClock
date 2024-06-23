package com.mioshek.theclock.data


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

fun getFullClockTime(elapsedTime: Long): ClockTime {
    val hours = (elapsedTime / (1000 * 60 * 60)) % 24
    val minutes = (elapsedTime / (1000 * 60)) % 60
    val seconds = (elapsedTime / 1000) % 60
    val milliseconds = elapsedTime % 1000
    return ClockTime(milliseconds, seconds, minutes, hours)
}

fun getClockTimeWithoutMillis(elapsedTime: Long): ClockTime {
    val hours = (elapsedTime / (1000 * 60 * 60)) % 24
    val minutes = (elapsedTime / (1000 * 60)) % 60
    val seconds = (elapsedTime / 1000) % 60
    return ClockTime(seconds = seconds, minutes = minutes, hours = hours)
}