package com.mioshek.theclock.db

import android.content.Context
import com.mioshek.theclock.db.models.AlarmsRepository
import com.mioshek.theclock.db.models.TimerRepository

interface AppContainer {
    val timerRepository: TimerRepository
    val alarmsRepository: AlarmsRepository
}

/**
 * [AppContainer] implementation that provides instance of [OfflineItemsRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [ItemsRepository]
     */
    override val timerRepository: TimerRepository by lazy {
        TimerRepository(AppDatabase.getDatabase(context).timerDao)
    }

    override val alarmsRepository: AlarmsRepository by lazy {
        AlarmsRepository(AppDatabase.getDatabase(context).alarmsDao)
    }
}