package com.mioshek.theclock.db

import android.content.Context

interface AppContainer {
    val timerRepository: TimerRepository
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
}