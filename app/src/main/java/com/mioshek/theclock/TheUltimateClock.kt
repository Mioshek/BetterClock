package com.mioshek.theclock

import android.app.Application
import com.mioshek.theclock.db.AppContainer
import com.mioshek.theclock.db.AppDataContainer
import com.mioshek.theclock.db.AppDatabase
import com.mioshek.theclock.db.TimerRepository

class TheUltimateClock: Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}