package com.app.base.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.base.data.model.AlarmModel
import com.app.base.helpers.AlarmHelper
import com.app.base.utils.LogUtil
import com.brally.mobile.base.viewmodel.BaseViewModel
import java.util.UUID

class NewAlarmViewModel : BaseViewModel() {

    private var _newAlarm = MutableLiveData<AlarmModel?>()
    val newAlarm: LiveData<AlarmModel?> = _newAlarm

    private var initialized = false

    fun initNewAlarmIfNeeded() {
        if (!initialized) {
            _newAlarm.value = AlarmModel(
                id = UUID.randomUUID().toString(),
                hour = 0,
                minute = 0,
                isOn = true,
                message = null,
                sound = null,
                datesOfWeek = null,
                date = null,
                character = null
            )
            initialized = true
        }
    }

    fun setAlarmOnce(alarm: AlarmModel) {
        if (!initialized) {
            _newAlarm.value = alarm
            initialized = true
        }
    }

    fun clearAlarm() {
        _newAlarm.value = null
        initialized = false
    }

    fun updateTime(hour: Int, minute: Int) {
        _newAlarm.value = _newAlarm.value?.copy(hour = hour, minute = minute)
    }

    fun updateDate(date: Long?) {
        _newAlarm.value = _newAlarm.value?.copy(date = date)
    }

    fun updateMessage(text: String) {
        _newAlarm.value = _newAlarm.value?.copy(message = text)
    }

    fun updateSound(sound: Int?) {
        _newAlarm.value = _newAlarm.value?.copy(sound = sound)
    }

    fun updateDatesOfWeek(dateOfWeek: List<Int>?) {
        _newAlarm.value = _newAlarm.value?.copy(datesOfWeek = dateOfWeek)
    }

    fun updateCharacter(character: Int?) {
        _newAlarm.value = _newAlarm.value?.copy(character = character)
    }

    fun isNoDateChoose() {
        val alarm = _newAlarm.value ?: return
        if (alarm.datesOfWeek == null && alarm.date == null) {
            LogUtil.log("ca 2 null")
            updateDate(AlarmHelper.getNearestTime(alarm.hour, alarm.minute))
        }
    }

    fun isChange(): Boolean {
        val initial = AlarmModel(
            id = _newAlarm.value!!.id,
            hour = 0,
            minute = 0,
            isOn = true,
            message = null,
            sound = null,
            datesOfWeek = null,
            date = null,
            character = null
        )
        return initial != _newAlarm.value
    }
}