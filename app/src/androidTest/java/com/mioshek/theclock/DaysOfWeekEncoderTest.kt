package com.mioshek.theclock

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mioshek.theclock.controllers.AlarmsListViewModel
import com.mioshek.theclock.db.AppDatabase
import com.mioshek.theclock.db.models.Alarms
import com.mioshek.theclock.db.models.AlarmsDao
import com.mioshek.theclock.db.models.AlarmsRepository
import kotlinx.coroutines.flow.Flow
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

class a: AlarmsDao{
    override suspend fun upsert(alarms: Alarms) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: Int) {
        TODO("Not yet implemented")
    }

    override fun getAllByIdDesc(): Flow<List<Alarms>> {
        TODO("Not yet implemented")
    }

    override fun getAllByIdAsc(): Flow<List<Alarms>> {
        TODO("Not yet implemented")
    }

    override fun getAllByTimeDesc(): Flow<List<Alarms>> {
        TODO("Not yet implemented")
    }

    override fun getAllByTimeAsc(): Flow<List<Alarms>> {
        TODO("Not yet implemented")
    }

}
@RunWith(AndroidJUnit4::class)
class DaysOfWeekEncoderTest {
    val alarmsListViewModel = AlarmsListViewModel(AlarmsRepository(alarmsDao = a())
    )
    @Test
    fun everydayTest() {
        val testedValue = alarmsListViewModel.encodeDaysOfWeek(arrayOf(true, true, true, true, true, true, true))
        assertEquals(127, testedValue)
    }
    @Test
    fun mondayTest() {
        val testedValue = alarmsListViewModel.encodeDaysOfWeek(arrayOf(true, false, false, false, false, false, false))
        assertEquals(64, testedValue)
    }

    @Test
    fun sundayTest() {
        val testedValue = alarmsListViewModel.encodeDaysOfWeek(arrayOf(false, false, false, false, false, false, true))
        assertEquals(1, testedValue)
    }

    @Test
    fun everySecondDayTest() {
        val testedValue = alarmsListViewModel.encodeDaysOfWeek(arrayOf(true, false, true, false, true, false, true))
        assertEquals(85, testedValue)
    }

    @Test
    fun noDayTest() {
        val testedValue = alarmsListViewModel.encodeDaysOfWeek(arrayOf(false, false, false, false, false, false, false))
        assertEquals(0, testedValue)
    }
}