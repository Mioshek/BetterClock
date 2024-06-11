package com.mioshek.theclock.controllers

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mioshek.theclock.data.ClockTime
import com.mioshek.theclock.data.TimingState
import com.mioshek.theclock.data.getTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.mioshek.theclock.controllers.TimerUiState as TimerUiState

data class TimerUiState(
    val index: Int,
    val time: ClockTime = ClockTime(),
    val timerState: TimingState = TimingState.OFF,
    val percentRemain: Int = 100
)


class TimerListViewModel: ViewModel() {
    private val _timers = mutableStateListOf<TimerUiState>(TimerUiState(0), TimerUiState(1))
    val timers: List<TimerUiState> = _timers

    fun runTimer(timer: TimerUiState) {
        // assuming the initial timer state is set from the UI
        // we calculate the future - the millis timer will finish
        val future = calculateFutureMillis(timer)
        val cycleTimeMs = 33L
        CoroutineScope(Dispatchers.Default).launch {
            while (System.currentTimeMillis() < future - cycleTimeMs && timer.timerState == TimingState.RUNNING) {
                val currentTime = System.currentTimeMillis()
                val remainingTime = future - currentTime

                val time = getTime(remainingTime)
                updateTimer(TimerUiState(timer.index, time, timer.timerState, timer.percentRemain))
                delay(cycleTimeMs) // 30FPS
            }
        }
        updateTimer(TimerUiState(timer.index, timer.time, TimingState.OFF, timer.percentRemain))
        // TIMER FINISHED IDFK reset it to 0, send notifications?, blast ears
    }

    fun updateTimer(timer: TimerUiState){
        _timers[timer.index] = timer
    }

    fun updateAllTimers(){
        viewModelScope.launch {
//            _timers.addAll(itemRepository.getItems())
        }
    }

    private fun calculateFutureMillis(timer: TimerUiState): Long {
        val time = timer.time
        return time.hours * 60 * 60 * 1000 + time.minutes * 60 * 1000 + time.seconds * 1000
    }
}
