package com.mioshek.theclock.controllers

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

data class AlarmsUiState(
    val id: Int = 0
)


class AlarmsListViewModel(): ViewModel() {
    private val _timers = mutableStateListOf<AlarmsUiState>()
    val timers: List<AlarmsUiState> = _timers
}