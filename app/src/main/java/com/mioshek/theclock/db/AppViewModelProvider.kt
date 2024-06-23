package com.mioshek.theclock.db

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mioshek.theclock.TheUltimateClock
import com.mioshek.theclock.controllers.TimerListViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            TimerListViewModel(
                theUltimateClockApplication().container.timerRepository,
            )
        }
    }
}

fun CreationExtras.theUltimateClockApplication(): TheUltimateClock =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TheUltimateClock)