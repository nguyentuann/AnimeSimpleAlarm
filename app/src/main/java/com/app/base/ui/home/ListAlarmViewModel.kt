package com.app.base.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.base.data.entity.toAlarmEntity
import com.app.base.data.entity.toAlarmModel
import com.app.base.data.model.AlarmModel
import com.app.base.local.dao.AppDAO
import com.app.base.ui.alarm.AlarmScheduler
import com.app.base.utils.LogUtil
import com.brally.mobile.base.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ListAlarmViewModel(
    private val appDAO: AppDAO,
    private val alarmScheduler: AlarmScheduler
) : BaseViewModel() {

    private val _alarmList = MutableLiveData<List<AlarmModel>>()
    val alarmList: LiveData<List<AlarmModel>> get() = _alarmList

    init {
        // Tải dữ liệu ban đầu từ DB bất đồng bộ
        launchHandler(onError = { err ->
            LogUtil.log("Load alarms error: $err")
        }) {
            val models = withContext(Dispatchers.IO) {
                appDAO.getAllAlarms().map { it.toAlarmModel() }
            }
            _alarmList.postValue(models)
        }
    }

    fun saveAlarm(alarmModel: AlarmModel) {
        launchHandler {
            withContext(Dispatchers.IO) {
                appDAO.addAlarm(alarmModel.toAlarmEntity())
                alarmScheduler.scheduleAlarm(alarmModel)
            }
            updateInMemoryList { current ->
                current + alarmModel
            }
        }
    }

    fun delete(alarmModel: AlarmModel) {
        launchHandler {
            withContext(Dispatchers.IO) {
                appDAO.deleteAlarm(alarmModel.toAlarmEntity())
                alarmScheduler.cancelAlarm(alarmModel)
            }
            updateInMemoryList { current ->
                current.filter { it.id != alarmModel.id }
            }
        }
    }

    fun active(alarmModel: AlarmModel, isActive: Boolean) {
        launchHandler {
            withContext(Dispatchers.IO) {
                appDAO.updateAlarmActiveState(alarmModel.id, isActive)

                // Update alarm schedule
                if (isActive) alarmScheduler.scheduleAlarm(alarmModel)
                else alarmScheduler.cancelAlarm(alarmModel)
            }
            val currentList = _alarmList.value?.toMutableList() ?: return@launchHandler
            val index = currentList.indexOfFirst { it.id == alarmModel.id }
            if (index != -1) {
                currentList[index].isOn = isActive  // giữ nguyên object
                _alarmList.value = currentList
            }
        }
    }

    fun getAlarmById(id: String): LiveData<AlarmModel?> {
        val found = _alarmList.value?.find { it.id == id }
        return MutableLiveData(found)
    }

    fun updateAlarm(alarm: AlarmModel) {
        launchHandler {
            withContext(Dispatchers.IO) {
                appDAO.updateAlarm(alarm.toAlarmEntity())
                alarmScheduler.editAlarm(alarm)
            }
            updateInMemoryList { current ->
                current.map { if (it.id == alarm.id) alarm else it }
            }
        }
    }

    // Helper để update danh sách trong LiveData an toàn
    private fun updateInMemoryList(update: (List<AlarmModel>) -> List<AlarmModel>) {
        val current = _alarmList.value ?: emptyList()
        _alarmList.postValue(update(current))
    }
}