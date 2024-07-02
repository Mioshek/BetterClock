package com.mioshek.theclock.controllers

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mioshek.theclock.data.ClockTime
import com.mioshek.theclock.data.TimingState
import com.mioshek.theclock.data.getClockTimeWithoutMillis
import com.mioshek.theclock.data.getFullClockTime
import com.mioshek.theclock.db.models.Timer
import com.mioshek.theclock.db.models.TimerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class TimerUiState(
    val id: Int = 0,
    val name: String = "",
    val initialTime: ClockTime = ClockTime(),
    val updatableTime: ClockTime = initialTime.copy(),
    val timerState: TimingState = TimingState.OFF,
    val remainingProgress: Float = 1f,
    val visible: Boolean = true
)

enum class SortType{
    ID_DESC,
    ID_ASC,
    TIME_DESC,
    TIME_ASC
}


class TimerListViewModel(
    private val repository: TimerRepository
): ViewModel() {
    private val _sortBy = mutableStateOf(SortType.ID_ASC)
    private val _timers = mutableStateListOf<TimerUiState>()
    val timers: List<TimerUiState> = _timers
    init {importTimers()}


    fun createTimer(timer: TimerUiState){
        val index = timers.size
        val newTimer = TimerUiState(id = index, initialTime = timer.initialTime)
        _timers.add(newTimer)
        viewModelScope.launch {
            repository.upsert(Timer(time = (timeToMillis(newTimer.initialTime)/1000).toInt(), dateCreated = System.currentTimeMillis()))
        }
    }

    fun deleteTimer(dbIndex: Int, uiIndex: Int){
        val updatedTimer = _timers[uiIndex].copy(visible = false)
        _timers[uiIndex] = updatedTimer
        CoroutineScope(Dispatchers.Default).launch{
            repository.delete(dbIndex)
        }
    }

    private fun importTimers(){
        viewModelScope.launch {
            val loadedTimers = when(_sortBy.value) {
                SortType.ID_DESC -> {repository.getAllByIdDesc().first()}
                SortType.ID_ASC -> {repository.getAllByIdDesc().first()}
                SortType.TIME_DESC -> {repository.getAllByIdDesc().first()}
                SortType.TIME_ASC -> {repository.getAllByIdDesc().first()}
            }

            for (timer in loadedTimers){
                _timers.add(
                    TimerUiState(
                        id = timer.id,
                        initialTime = getClockTimeWithoutMillis(timer.time.toLong()*1000)
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

    fun runTimer(uiIndex: Int) {
        // assuming the initial timer state is set from the UI
        // we calculate the future - the millis timer will finish
        var timer = _timers[uiIndex]
        var progressBarStatus: Float
        val timerTime = timeToMillis(timer.initialTime)
        var future = timeToMillis(timer.updatableTime) + System.currentTimeMillis()
        var time: ClockTime
        val cycleTimeMs = 17L

        CoroutineScope(Dispatchers.Default).launch {
            while (System.currentTimeMillis() < future - cycleTimeMs && timer.timerState == TimingState.RUNNING) {
                timer = _timers[uiIndex]
                val currentTime = System.currentTimeMillis()
                val remainingTime = future - currentTime
                time = getFullClockTime(remainingTime)
                progressBarStatus = remainingTime.toFloat() / timerTime.toFloat()
                updateTimer(uiIndex, timer.copy(updatableTime = time, remainingProgress = progressBarStatus))
                delay(cycleTimeMs) // 60FPS
                while (timer.timerState == TimingState.PAUSED){
                    timer = _timers[uiIndex]
                    future = timeToMillis(timer.updatableTime) + System.currentTimeMillis()
                    delay(100)
                }
            }
            if(timer.timerState == TimingState.RUNNING){
                Log.d("Notification", "AlarmFinished")
                updateTimer(index = uiIndex, TimerUiState(id = timer.id, initialTime = timer.initialTime))
                // Notification
            }
            if (timer.timerState == TimingState.OFF){
                updateTimer(index = uiIndex, TimerUiState(id = timer.id, initialTime = timer.initialTime))
            }
        }
    }

    fun updateTimer(index: Int, timer: TimerUiState){
        _timers[index] = timer
    }

    fun resumeTimer(uiIndex: Int){
        val timer = _timers[uiIndex]
        val newTimer = timer.copy(timerState = TimingState.RUNNING)
        updateTimer(uiIndex, newTimer)
    }

    private fun timeToMillis(time: ClockTime): Long {
        return time.hours * 60 * 60 * 1000 + time.minutes * 60 * 1000 + time.seconds * 1000 + time.milliseconds
    }
}