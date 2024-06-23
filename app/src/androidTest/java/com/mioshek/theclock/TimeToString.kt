package com.mioshek.theclock

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mioshek.theclock.assets.StringFormatters.Companion.getStringTime
import com.mioshek.theclock.data.ClockTime
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TimeToString {

    @Test
    fun zeroTimeToStringTest(){
        val time = ClockTime(0,0,0,0)
        val stringTime = getStringTime(time, 0,4)
        println(stringTime)
        assertEquals(stringTime, "00:00.000")
    }

    @Test
    fun fullTimeToStringTest(){
        val time = ClockTime(25,9,12,3)
        val stringTime = getStringTime(time, 0,4)
        println(stringTime)
        assertEquals(stringTime, "03:12:09.025")
    }

    @Test
    fun withoutMillisTimeToStringTest(){
        val time = ClockTime(251,15,1,1)
        val stringTime = getStringTime(time, 0,3)
        println(stringTime)
        assertEquals(stringTime, "01:01:15")
    }

    @Test
    fun zeroHoursTimeToStringTest(){
        val time = ClockTime(251,15,0,0)
        val stringTime = getStringTime(time, 0,4)
        println(stringTime)
        assertEquals(stringTime, "00:15.251")
    }
}