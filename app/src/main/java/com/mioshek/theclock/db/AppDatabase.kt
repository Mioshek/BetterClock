package com.mioshek.theclock.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mioshek.theclock.db.models.Alarms
import com.mioshek.theclock.db.models.AlarmsDao
import com.mioshek.theclock.db.models.AlarmsRepository
import com.mioshek.theclock.db.models.Timer
import com.mioshek.theclock.db.models.TimerDao

@Database(entities = [Timer::class, Alarms::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract val timerDao: TimerDao
    abstract val alarmsDao: AlarmsDao

    companion object {
        // Singleton prevents multiple instances of database opening at the same time.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "the_ultimate_clock_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        fun closeDb(){
            INSTANCE?.close()
        }
    }
}