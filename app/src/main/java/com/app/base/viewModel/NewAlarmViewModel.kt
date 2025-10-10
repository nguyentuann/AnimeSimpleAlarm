package com.app.base.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.base.data.model.AlarmModel
import com.app.base.helpers.AlarmHelper
import com.brally.mobile.base.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class NewAlarmViewModel : BaseViewModel() {

    private val _newAlarm = MutableLiveData<AlarmModel>(
        AlarmModel(
            id = UUID.randomUUID().toString(),
            hour = 0,
            minute = 0,
            isOn = true,
            message = null,
            sound = null,
            dateOfWeek = null,
            date = null,
            character = null
        )
    )

    val newAlarm: LiveData<AlarmModel> = _newAlarm

    fun updateTime(hour: Int, minute: Int) {
        _newAlarm.value = _newAlarm.value?.copy(hour = hour, minute = minute)
    }

    fun updateDate(date: Long?) {
        _newAlarm.value = _newAlarm.value?.copy(date = date)
    }


    fun updateMessage(text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _newAlarm.postValue(_newAlarm.value?.copy(message = text))
        }
    }

    fun updateSound(sound: Int?) {
        _newAlarm.value = _newAlarm.value?.copy(sound = sound)
    }

    fun updateDateOfWeek(dateOfWeek: List<Int>?) {
        _newAlarm.value = _newAlarm.value?.copy(dateOfWeek = dateOfWeek)
    }

    fun updateCharacter(character: Int?) {
        _newAlarm.value = _newAlarm.value?.copy(character = character)
    }

    fun resetAlarm() {
        _newAlarm.value = AlarmModel(
            id = UUID.randomUUID().toString(),
            hour = 0,
            minute = 0,
            isOn = true,
            message = null,
            sound = null,
            dateOfWeek = null,
            date = null,
            character = null
        )
    }

    fun prepareAlarmBeforeSave(selectedDays: Set<Int>, message: String, sound: Int) {
        val alarm = _newAlarm.value ?: return

        when {
            alarm.date != null -> updateDateOfWeek(null)
            selectedDays.isEmpty() -> updateDate(AlarmHelper.getTomorrowDate())
            else -> updateDateOfWeek(selectedDays.sorted())
        }

        updateMessage(message)
        updateSound(sound)
    }

    fun isChange(): Boolean {
        val initial = AlarmModel(
            id = _newAlarm.value!!.id,
            hour = 0,
            minute = 0,
            isOn = true,
            message = null,
            sound = null,
            dateOfWeek = null,
            date = null,
            character = null
        )
        return initial != _newAlarm.value
    }

    fun setAlarm(alarm: AlarmModel) {
        _newAlarm.value = alarm
    }

}