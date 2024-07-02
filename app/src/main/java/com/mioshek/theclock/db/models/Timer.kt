package com.mioshek.theclock.db.models

import androidx.annotation.WorkerThread
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "Timer")
data class Timer(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val time: Int,
    val dateCreated: Long
)

@Dao
interface TimerDao{
    @Upsert
    suspend fun upsert(timer: Timer)

    @Query("DELETE FROM Timer WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM Timer ORDER BY id DESC ")
    fun getAllByIdDesc(): Flow<List<Timer>>

    @Query("SELECT * FROM Timer ORDER BY id ASC ")
    fun getAllByIdAsc(): Flow<List<Timer>>

    @Query("SELECT * FROM Timer ORDER BY time DESC ")
    fun getAllByTimeDesc(): Flow<List<Timer>>

    @Query("SELECT * FROM Timer ORDER BY time ASC ")
    fun getAllByTimeAsc(): Flow<List<Timer>>

}


class TimerRepository(private val timerDao: TimerDao) {
    @WorkerThread suspend fun upsert(timer: Timer) = timerDao.upsert(timer)

    @WorkerThread suspend fun delete(id: Int) = timerDao.delete(id)

    fun getAllByIdDesc(): Flow<List<Timer>> = timerDao.getAllByIdDesc()

    fun getAllByIdAsc(): Flow<List<Timer>> = timerDao.getAllByIdAsc()

    fun getAllByTimeDesc(): Flow<List<Timer>> = timerDao.getAllByTimeDesc()

    fun getAllByTimeAsc(): Flow<List<Timer>> = timerDao.getAllByTimeAsc()
}