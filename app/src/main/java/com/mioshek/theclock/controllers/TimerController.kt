package com.mioshek.theclock.controllers

import com.mioshek.theclock.services.NotificationsManager
import android.app.Application
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.Service
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat.ServiceNotificationBehavior
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mioshek.theclock.R
import com.mioshek.theclock.data.ClockTime
import com.mioshek.theclock.data.TimeFormatter.Companion.getClockTimeWithoutMillis
import com.mioshek.theclock.data.TimeFormatter.Companion.getFullClockTime
import com.mioshek.theclock.data.TimingState
import com.mioshek.theclock.db.models.Timer
import com.mioshek.theclock.db.models.TimerRepository
import com.mioshek.theclock.services.RingtoneService
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
    private val repository: TimerRepository,
    private val application: Application,
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
            repository.upsert(Timer(time = (timeToMillis(newTimer.initialTime)/1000).toInt()))
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
                startRingtoneService()
//                while (timer.timerState != TimingState.OFF){
//                    Log.d("TimingState", "${timer.timerState}")
//                }
                stopRingtoneService()
                updateTimer(index = uiIndex, TimerUiState(id = timer.id, initialTime = timer.initialTime))
            }
            if (timer.timerState == TimingState.OFF){
                updateTimer(index = uiIndex, TimerUiState(id = timer.id, initialTime = timer.initialTime))
            }
        }
    }

    private fun startRingtoneService(){
        val intent = Intent(application.applicationContext, RingtoneService::class.java)
        ContextCompat.startForegroundService(application.applicationContext, intent)
    }

    private fun stopRingtoneService(){
        val intent = Intent(application.applicationContext, RingtoneService::class.java)
        application.applicationContext.stopService(intent)
    }

    fun updateTimer(index: Int, timer: TimerUiState){
        _timers[index] = timer
        viewModelScope.launch {
            repository.upsert(
                Timer(
                    id = timer.id,
                    time = (timeToMillis(timer.initialTime)/1000).toInt(),
                )
            )
        }
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