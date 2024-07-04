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

        fun format(timeInMinutes: Int, as24h: Boolean): Array<String> {
            val hour = timeInMinutes / 60
            val minute = timeInMinutes % 60
            val formattedMinutes = if (minute > 9) minute else "0$minute"
            if (as24h) {
                if (hour < 10) return arrayOf(" $hour:$formattedMinutes")
                return arrayOf("$hour:$formattedMinutes")
            }
            if (hour == 0) return arrayOf("12:$formattedMinutes","AM")
            if (hour < 10) return arrayOf(" $hour:$formattedMinutes", "AM")
            if (hour < 12) return arrayOf("$hour:$formattedMinutes","AM")
            return if (hour == 12) arrayOf("12:$formattedMinutes}","PM")
            else{
                if ((hour - 12) < 10) return arrayOf((hour - 12).toString() + ":" + formattedMinutes, "PM")
                return arrayOf((hour - 12).toString() + ":" + formattedMinutes, "PM")
            }
        }
    }
}