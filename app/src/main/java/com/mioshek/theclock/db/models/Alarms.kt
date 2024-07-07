package com.mioshek.theclock.db.models

import androidx.annotation.WorkerThread
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


/**
 * @param daysOfWeek counting starts on Sunday like so:
 *
 * 1 - Only sunday
 *
 * 10 - Only saturday
 *
 * 100 - Only friday
 *
 * 1000 - only thursday etc.
 *
 * 1111111 - means everyday
 */
@Entity(tableName = "Alarms")
data class Alarms(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String?,
    val time: Int,
    val daysOfWeek: Int,
    val sound: String?,
    val enabled: Boolean,
)

@Dao
interface AlarmsDao{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(alarm: Alarms): Long

    @Update
    suspend fun update(alarm: Alarms)

    @Transaction
    suspend fun upsert(alarm: Alarms) {
        val id = insert(alarm)
        if (id == -1L) {
            update(alarm)
        }
    }

    @Query("DELETE FROM Alarms WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM Alarms ORDER BY id DESC ")
    fun getAllByIdDesc(): Flow<List<Alarms>>

    @Query("SELECT * FROM Alarms ORDER BY id ASC ")
    fun getAllByIdAsc(): Flow<List<Alarms>>

    @Query("SELECT * FROM Alarms ORDER BY time DESC ")
    fun getAllByTimeDesc(): Flow<List<Alarms>>

    @Query("SELECT * FROM Alarms ORDER BY time ASC ")
    fun getAllByTimeAsc(): Flow<List<Alarms>>

}


class AlarmsRepository(private val alarmsDao: AlarmsDao) {
    @WorkerThread suspend fun upsert(alarm: Alarms) = alarmsDao.upsert(alarm)
    
    @WorkerThread suspend fun delete(id: Int) = alarmsDao.delete(id)

    fun getAllByIdDesc(): Flow<List<Alarms>> = alarmsDao.getAllByIdDesc()

    fun getAllByIdAsc(): Flow<List<Alarms>> = alarmsDao.getAllByIdAsc()

    fun getAllByTimeDesc(): Flow<List<Alarms>> = alarmsDao.getAllByTimeDesc()

    fun getAllByTimeAsc(): Flow<List<Alarms>> = alarmsDao.getAllByTimeAsc()
}