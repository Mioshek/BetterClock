package com.mioshek.theclock.controllers

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mioshek.theclock.db.models.Alarms
import com.mioshek.theclock.db.models.AlarmsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class AlarmsUiState(
    val id: Int = 0,
    val name: String? = null,
    val time: Int = 0,
    val daysOfWeek: Array<Boolean> = Array(7) { false },
    val sound: String? = null,
    val enabled: Boolean,
    val visible: Boolean = true
)


class AlarmsListViewModel(private val alarmsRepository: AlarmsRepository): ViewModel() {
    private val _alarms = mutableStateListOf<AlarmsUiState>()
    val alarms: List<AlarmsUiState> = _alarms

    init {getAllAlarms()}


    fun toggleAlarm(index: Int){
        val previousState = _alarms[index]
        _alarms[index] = previousState.copy(enabled = !previousState.enabled)
        //Send to system clock
    }

    private fun getAllAlarms(){
        viewModelScope.launch {
            val importedAlarms = alarmsRepository.getAllByIdAsc().first()

            for (alarm in importedAlarms){
                _alarms.add(
                    AlarmsUiState(
                        id = alarm.id,
                        name = alarm.name,
                        time = alarm.time,
                        daysOfWeek = decodeDaysOfWeek(alarm.daysOfWeek),
                        sound = alarm.sound,
                        enabled = alarm.enabled,
                    )
                )
            }
        }
    }

    fun upsert(alarm: AlarmsUiState){
        _alarms.add(alarm)
        viewModelScope.launch {
            alarmsRepository.upsert(
                Alarms(
                    name = alarm.name,
                    time = alarm.time,
                    daysOfWeek = 1,
                    sound = alarm.sound,
                    enabled = alarm.enabled
                )
            )
        }
    }

    fun deleteAlarm(index: Int){
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