package com.mioshek.theclock.controllers

import androidx.lifecycle.ViewModel
import com.mioshek.theclock.data.ClockTime
import com.mioshek.theclock.data.Storage
import com.mioshek.theclock.data.TimeFormatter.Companion.getFullClockTime
import com.mioshek.theclock.data.TimingState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class StopwatchUiState(
    val time: ClockTime = ClockTime(),
    val stopwatchState: TimingState = TimingState.OFF,
    val stages: MutableList<ClockTime> = mutableListOf()
)


class StopwatchViewModel: ViewModel(){
    private val _stopwatchUiState = MutableStateFlow(StopwatchUiState())
    val stopwatchUiState: StateFlow<StopwatchUiState> = _stopwatchUiState.asStateFlow()

    fun changeStopwatchState(stopwatchState: TimingState){
        _stopwatchUiState.update {currentState ->
            currentState.copy(
                stopwatchState = stopwatchState
            )
        }
    }

    fun runStopwatch(){
        val pausedTime = _stopwatchUiState.value.time.hours * 3600000 + _stopwatchUiState.value.time.minutes * 60000 + _stopwatchUiState.value.time.seconds * 1000 + _stopwatchUiState.value.time.milliseconds

        CoroutineScope(Dispatchers.Default).launch {
            val startTime = System.currentTimeMillis()
            while (_stopwatchUiState.value.time.hours < 100 && _stopwatchUiState.value.stopwatchState == TimingState.RUNNING){
                val currentTime = System.currentTimeMillis()
                val elapsedTime = currentTime - startTime + pausedTime
                val time = getFullClockTime(elapsedTime)
                _stopwatchUiState.update {currentState ->
                    currentState.copy(
                        time = time
                    )
                }
                delay(33) // 30FPS
            }
            val endTime = Storage.take<Long>("EndTime")
            val finalElapsedTime = endTime - startTime + pausedTime
            val time = getFullClockTime(finalElapsedTime)
            _stopwatchUiState.update {currentState ->
                currentState.copy(
                    time = time
                )
            }
        }
    }

    fun resumeStopwatch(){
        runStopwatch()
    }

    fun addStage(){ // Create view for flags
        val stage = _stopwatchUiState.value.time
        _stopwatchUiState.update { currentState ->
            val stages = currentState.stages
            stages.add(stage)

            currentState.copy(
                stages = stages
            )
        }
    }

    fun resetStopwatch(){
        _stopwatchUiState.update { currentState ->
            currentState.copy(
                time = ClockTime(),
                stopwatchState = TimingState.OFF,
                stages = mutableListOf()
            )
        }
    }
}