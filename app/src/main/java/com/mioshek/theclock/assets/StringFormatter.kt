package com.mioshek.theclock.assets

import com.mioshek.theclock.data.ClockTime
import kotlin.math.round

class StringFormatters {

    companion object{
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

        private fun padValue(str: StringBuilder, value: Int, digits: Int) {
            var digits = digits
            val strValue = value.toString()
            digits -= strValue.length
            while (digits > 0) {
                str.append('0')
                digits--
            }
            str.append(strValue)
        }

        /**
         * Formats Float [factor] to String Value.
         *
         * Examples:
         * 1.0f -> 100%
         *
         * 0.8347673f -> 83%
         *
         * 0.03642f -> 3.6%
         * @return String value of [factor]
         * */
        fun formatPercentage(factor: Float): String {
            return if (factor >= 0.1f) {
                ((factor * 100.0).toInt()).toString()
            } else {
                (round(factor * 1000.0) / 10.0).toString()
            }
        }
    }
}