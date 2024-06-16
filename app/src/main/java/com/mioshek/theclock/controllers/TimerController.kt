package com.mioshek.theclock.controllers

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mioshek.theclock.data.ClockTime
import com.mioshek.theclock.data.TimingState
import com.mioshek.theclock.data.getTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.mioshek.theclock.controllers.TimerUiState as TimerUiState

data class TimerUiState(
    val id: Int,
    val updatableTime: ClockTime = ClockTime(),
    val initialTime: ClockTime = ClockTime(),
    val timerState: TimingState = TimingState.OFF,
    val remainingProgress: Float = 1f
)


class TimerListViewModel: ViewModel() {
    private val _timers = mutableStateListOf<TimerUiState>(TimerUiState(0), TimerUiState(1))
    val timers: List<TimerUiState> = _timers

    fun runTimer(timerIndex: Int) {
        // assuming the initial timer state is set from the UI
        // we calculate the future - the millis timer will finish
        var timer = _timers[timerIndex]
        var progressBarStatus = timer.remainingProgress
        val timerTime = timeToMillis(timer.initialTime)
        val future = timeToMillis(timer.updatableTime) + System.currentTimeMillis()
        var time = timer.updatableTime
        val cycleTimeMs = 17L

        CoroutineScope(Dispatchers.Default).launch {
            while (System.currentTimeMillis() < future - cycleTimeMs && timer.timerState == TimingState.RUNNING) {
                timer = _timers[timerIndex]
                val currentTime = System.currentTimeMillis()
                val remainingTime = future - currentTime
                time = getTime(remainingTime)
                progressBarStatus = remainingTime.toFloat() / timerTime.toFloat()
                updateTimer(TimerUiState(timer.id, time, timer.initialTime, timer.timerState, progressBarStatus))
                delay(cycleTimeMs) // 30FPS
            }
            updateTimer(TimerUiState(timer.id, time, timer.initialTime, TimingState.PAUSED, progressBarStatus))
        }
    }

    fun updateTimer(timer: TimerUiState){
        _timers[timer.id] = timer
    }

    fun updateAllTimers(){
        viewModelScope.launch {
//            _timers.addAll(itemRepository.getItems())
        }
    }

    private fun timeToMillis(time: ClockTime): Long {
        return time.hours * 60 * 60 * 1000 + time.minutes * 60 * 1000 + time.seconds * 1000
    }
}
