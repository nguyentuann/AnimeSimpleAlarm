package com.app.base.viewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.app.base.data.model.AlarmModel
import kotlinx.coroutines.launch
import com.app.base.repository.AlarmRepository
import com.brally.mobile.base.viewmodel.BaseViewModel

class ListAlarmViewModel(
    private val repository: AlarmRepository,
) : BaseViewModel() {

    val alarmList: LiveData<List<AlarmModel>> = repository.getAllAlarms()

    fun saveAlarm(alarm: AlarmModel) {
        viewModelScope.launch {
            repository.addAlarm(alarm)
        }
    }

    fun delete(alarm: AlarmModel) {
        viewModelScope.launch {
            repository.deleteAlarm(alarm)
        }
    }

    fun active(alarm: AlarmModel, isEnable: Boolean) {
        viewModelScope.launch {
            repository.activeAlarm(alarm, isEnable)
        }
    }

    fun getAlarmById(id: String): LiveData<AlarmModel?> {
        return repository.getAlarmById(id)
    }

    fun updateAlarm(alarm: AlarmModel) {
        viewModelScope.launch {
            repository.updateAlarm(alarm)
        }
    }

}
