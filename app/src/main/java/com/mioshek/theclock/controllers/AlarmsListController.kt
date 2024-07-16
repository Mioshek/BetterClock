package com.mioshek.theclock.controllers

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mioshek.theclock.data.TimeFormatter
import com.mioshek.theclock.db.models.Alarms
import com.mioshek.theclock.db.models.AlarmsRepository
import com.mioshek.theclock.services.AlarmService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class AlarmUiState(
    val id: Int? = null,
    val name: String? = null,
    val initialTime: Int = 0,
    val ringTime: Int = initialTime,
    val daysOfWeek: Array<Boolean> = Array(7) { false },
    val sound: String? = null,
    val enabled: Boolean = false,
    val visible: Boolean = true,
    val isSelected: Boolean = false
)


class AlarmsListViewModel(
    private val alarmsRepository: AlarmsRepository,
    private val application: Application
) : ViewModel() {
    private val _alarm = MutableStateFlow(AlarmUiState())
    val alarm: StateFlow<AlarmUiState> = _alarm.asStateFlow()
    private val _alarms = mutableStateListOf<AlarmUiState>()
    val alarms: List<AlarmUiState> = _alarms
    private val alarmService = AlarmService(application)

    var inSelectionMode = mutableStateOf(false)

    init {
        getAllAlarms()
    }

    private fun getAllAlarms() {
        viewModelScope.launch {
            val importedAlarms = alarmsRepository.getAllByTimeAsc().first()
            _alarms.clear()
            for (alarm in importedAlarms) {
                _alarms.add(
                    AlarmUiState(
                        id = alarm.id,
                        name = alarm.name,
                        initialTime = alarm.time,
                        daysOfWeek = decodeDaysOfWeek(alarm.daysOfWeek),
                        sound = alarm.sound,
                        enabled = alarm.enabled,
                    )
                )
            }
        }
    }

    fun changeUiState(alarm: AlarmUiState) { _alarm.value = alarm }

    fun toggleAlarm(index: Int) {
        val previousState = _alarms[index]
        val newAlarm = previousState.copy(enabled = !previousState.enabled)
        _alarms[index] = newAlarm
        saveDb(newAlarm)
        if (newAlarm.enabled){
            alarmService.setAlarm(application.applicationContext,newAlarm.initialTime, newAlarm.daysOfWeek)
            Toast.makeText(
                application.applicationContext,
                "Clock set in ${TimeFormatter.calculateTimeLeft(newAlarm.initialTime, newAlarm.daysOfWeek)}",
                Toast.LENGTH_LONG
            ).show()
        }
        else{
            alarmService.cancelAlarm(application.applicationContext)
        }
    }

    private fun saveDb(alarm: AlarmUiState){
        viewModelScope.launch {
            alarmsRepository.upsert(
                Alarms(
                    id = alarm.id ?: 0,
                    name = alarm.name,
                    time = alarm.initialTime,
                    daysOfWeek = encodeDaysOfWeek(alarm.daysOfWeek),
                    sound = alarm.sound,
                    enabled = alarm.enabled
                )
            )
        }
    }

    fun upsert(alarm: AlarmUiState) {
        saveDb(alarm)
        getAllAlarms()
    }

    fun deleteAlarm(index: Int) {
        val deleted = _alarms[index]
        _alarms[index] = deleted.copy(visible = false)
        viewModelScope.launch {
            alarmsRepository.delete(deleted.id!!)
        }
    }

    fun changeAlarmSelection(index: Int, alarm: AlarmUiState) {_alarms[index] = alarm}

    fun changeSelectionState(){
        inSelectionMode.value = !inSelectionMode.value
        if (!inSelectionMode.value){
            _alarms.forEachIndexed{index, alarm ->
                _alarms[index] = alarm.copy(isSelected = false)
            }
        }
    }

    fun encodeDaysOfWeek(daysOfWeek: Array<Boolean>): Int {
        var encodedDays = 0
        for ((index, shouldRing) in daysOfWeek.withIndex()) {
            if (shouldRing) {
                encodedDays += 1 shl (daysOfWeek.size - 1 - index)
            }
        }
        return encodedDays
    }

    fun decodeDaysOfWeek(daysOfWeek: Int, size: Int = 7): Array<Boolean> {
        return Array(size) { index ->
            (daysOfWeek shr (size - 1 - index) and 1) == 1
        }
    }
}
