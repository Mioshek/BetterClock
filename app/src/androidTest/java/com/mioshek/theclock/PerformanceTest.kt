package com.mioshek.theclock

import android.util.Range
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mioshek.theclock.assets.StringFormatters.Companion.getStringTime
import com.mioshek.theclock.data.ClockTime
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Random


@RunWith(AndroidJUnit4::class)
class PerformanceTest {

    @Test
    fun timeToStringTest(){
        var avgTime = 0L
        val iterations = 1_000_000
        val clocks = createRandomClocks(iterations)

        for (i in 0..<iterations){
            val startTime = System.nanoTime()

            val stringTime = getStringTime(clocks[i], 1,4)

            val endTime = System.nanoTime()
            val elapsedTime = endTime - startTime
            avgTime += elapsedTime
        }

        println("Frisk Test: ${avgTime/iterations}ns")

    }

    private fun createRandomClocks(iterations: Int): List<ClockTime> {
        val random = Random()

        val clocks = mutableListOf<ClockTime>()
        for (i in 1..iterations) {
            clocks.add(ClockTime(
                random.nextInt(3).toLong(),  // hours
                random.nextInt(60).toLong(),  // minutes
                random.nextInt(60).toLong(),  // seconds
                random.nextInt(1000).toLong() // millis
            ))
        }
        return clocks.toList()
    }
}