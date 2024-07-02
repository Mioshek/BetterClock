package com.mioshek.theclock

import android.app.Application
import com.mioshek.theclock.db.AppContainer
import com.mioshek.theclock.db.AppDataContainer

class TheUltimateClock: Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}