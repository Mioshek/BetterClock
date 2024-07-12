package com.mioshek.theclock

import com.mioshek.theclock.data.ClockTime
import org.junit.Test
import org.junit.Assert.*

class ClockTimeTest {
    @Test
    fun zeroMinusZeroTest(){
        val tested = ClockTime() - ClockTime()
        assertEquals(ClockTime(), tested)
    }

    @Test
    fun realValueTest(){
        val tested = ClockTime(20,30,40,50) - ClockTime(10,20,30,40)
        assertEquals(ClockTime(10,10,10,10), tested)
    }

    @Test
    fun negativeValueTest(){
        val tested = ClockTime(20,30,40,10) - ClockTime(10,20,30,40)
        assertEquals(ClockTime(0,0,0,0), tested)
    }
}