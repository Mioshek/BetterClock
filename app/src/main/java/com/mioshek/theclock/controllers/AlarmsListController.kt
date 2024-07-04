package com.mioshek.theclock.controllers

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mioshek.theclock.db.models.Alarms
import com.mioshek.theclock.db.models.AlarmsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class AlarmUiState(
    val id: Int = 0,
    val name: String? = null,
    val initialTime: Int = 0,
    val ringTime: Int = initialTime,
    val daysOfWeek: Array<Boolean> = Array(7) { false },
    val sound: String? = null,
    val enabled: Boolean = false,
    val visible: Boolean = true
)


class AlarmsListViewModel(private val alarmsRepository: AlarmsRepository) : ViewModel() {
    private val _alarm = MutableStateFlow(AlarmUiState())
    val alarm: StateFlow<AlarmUiState> = _alarm.asStateFlow()
    private val _alarms = mutableStateListOf<AlarmUiState>()
    val alarms: List<AlarmUiState> = _alarms

    init {
        getAllAlarms()
    }

    private fun getAllAlarms() {
        viewModelScope.launch {
            val importedAlarms = alarmsRepository.getAllByIdAsc().first()
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

    fun changeUiState(alarm: AlarmUiState) {
        _alarm.value = alarm
    }

    fun toggleAlarm(index: Int) {
        val previousState = _alarms[index]
        _alarms[index] = previousState.copy(enabled = !previousState.enabled)
        //Send to system clock
    }

    fun upsert(alarm: AlarmUiState) {
        _alarms.add(alarm)
        viewModelScope.launch {
            alarmsRepository.upsert(
                Alarms(
                    name = alarm.name,
                    time = alarm.initialTime,
                    daysOfWeek = encodeDaysOfWeek(alarm.daysOfWeek),
                    sound = alarm.sound,
                    enabled = alarm.enabled
                )
            )
        }
    }

    fun deleteAlarm(index: Int) {
        val deleted = _alarms[index]
        _alarms[index] = deleted.copy(visible = false)
        viewModelScope.launch {
            alarmsRepository.delete(deleted.id)
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
