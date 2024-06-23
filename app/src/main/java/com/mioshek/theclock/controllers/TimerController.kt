package com.mioshek.theclock.controllers

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mioshek.theclock.data.ClockTime
import com.mioshek.theclock.data.TimingState
import com.mioshek.theclock.data.getClockTimeWithoutMillis
import com.mioshek.theclock.data.getFullClockTime
import com.mioshek.theclock.db.Timer
import com.mioshek.theclock.db.TimerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class TimerUiState(
    val id: Int = 0,
    val initialTime: ClockTime = ClockTime(),
    val updatableTime: ClockTime = initialTime.copy(),
    val timerState: TimingState = TimingState.OFF,
    val remainingProgress: Float = 1f
)


class TimerListViewModel(
    private val repository: TimerRepository
): ViewModel() {
    private val _timers = mutableStateListOf<TimerUiState>()
    val timers: List<TimerUiState> = _timers

    fun createTimer(timer: TimerUiState){
        val index = timers.size
        val newTimer = TimerUiState(id = index, initialTime = timer.initialTime)
        _timers.add(newTimer)
        viewModelScope.launch {
            repository.upsert(Timer(time = (timeToMillis(newTimer.initialTime)/1000).toInt(), dateCreated = System.currentTimeMillis()))
        }
    }

    fun deleteTimer(timer: TimerUiState){
        _timers.remove(timer)
        CoroutineScope(Dispatchers.Default).launch{
            repository.delete(timer.id)
        }
    }

    fun importTimers(){
        viewModelScope.launch {
            val timers = listOf<Timer>()
            for (timer in timers){
                _timers.add(
                    TimerUiState(
                        id = timer.id,
                        initialTime = getClockTimeWithoutMillis(timer.time.toLong())
                    )
                )
            }
        }
    }

    fun editTimer(timer: TimerUiState){
        _timers[timer.id] = timer
        viewModelScope.launch {
            repository.upsert(Timer(timer.id, timeToMillis(timer.initialTime).toInt(), System.currentTimeMillis()))
        }
    }

    fun runTimer(timerIndex: Int) {
        // assuming the initial timer state is set from the UI
        // we calculate the future - the millis timer will finish
        var timer = _timers[timerIndex]
        var progressBarStatus: Float
        val timerTime = timeToMillis(timer.initialTime)
        var future = timeToMillis(timer.updatableTime) + System.currentTimeMillis()
        var time: ClockTime
        val cycleTimeMs = 17L

        CoroutineScope(Dispatchers.Default).launch {
            while (System.currentTimeMillis() < future - cycleTimeMs && timer.timerState == TimingState.RUNNING) {
                timer = _timers[timerIndex]
                val currentTime = System.currentTimeMillis()
                val remainingTime = future - currentTime
                time = getFullClockTime(remainingTime)
                progressBarStatus = remainingTime.toFloat() / timerTime.toFloat()
                updateTimer(TimerUiState(timer.id, timer.initialTime, time, timer.timerState, progressBarStatus))
                delay(cycleTimeMs) // 60FPS
                while (timer.timerState == TimingState.PAUSED){
                    timer = _timers[timerIndex]
                    future = timeToMillis(timer.updatableTime) + System.currentTimeMillis()
                    delay(100)
                }
            }
            if(timer.timerState == TimingState.RUNNING){
                updateTimer(TimerUiState(id = timer.id, initialTime = timer.initialTime))
                // Notification
            }
        }
    }

    fun updateTimer(timer: TimerUiState){
        _timers[timer.id] = timer
    }

    fun resumeTimer(timerIndex: Int){
        val timer = _timers[timerIndex]
        val newTimer = TimerUiState(timer.id, timer.initialTime, timer.updatableTime, TimingState.RUNNING, timer.remainingProgress)
        updateTimer(newTimer)
    }

    private fun timeToMillis(time: ClockTime): Long {
        return time.hours * 60 * 60 * 1000 + time.minutes * 60 * 1000 + time.seconds * 1000 + time.milliseconds
    }
}