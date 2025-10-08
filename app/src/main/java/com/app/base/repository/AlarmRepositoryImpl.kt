package com.app.base.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.base.data.entity.toAlarmEntity
import com.app.base.data.entity.toAlarmModel
import com.app.base.data.model.AlarmModel
import com.app.base.local.dao.AppDAO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.app.base.ui.alarm.AlarmScheduler
import kotlinx.coroutines.SupervisorJob

class AlarmRepositoryImpl(
    private val appDAO: AppDAO,
    private val alarmScheduler: AlarmScheduler
) : AlarmRepository {
    private val alarmList = MutableLiveData<List<AlarmModel>>()

    init {
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            val models = appDAO.getAllAlarms().map { it.toAlarmModel() }
            alarmList.postValue(models)
        }
    }


    override fun getAllAlarms(): LiveData<List<AlarmModel>> = alarmList

    override suspend fun addAlarm(alarmModel: AlarmModel) {
        alarmList.value = alarmList.value?.toMutableList()?.apply { add(alarmModel) }
        appDAO.addAlarm(alarmModel.toAlarmEntity())
        alarmScheduler.scheduleAlarm(alarmModel)
    }

    override suspend fun deleteAlarm(alarmModel: AlarmModel) {
        alarmList.value = alarmList.value?.filter { it.id != alarmModel.id }
        appDAO.deleteAlarm(alarmModel.toAlarmEntity())
        alarmScheduler.cancelAlarm(alarmModel)
    }

    override suspend fun updateAlarm(alarm: AlarmModel) {
        alarmList.value = alarmList.value?.map { if (it.id == alarm.id) alarm else it }
        appDAO.updateAlarm(alarm.toAlarmEntity())
        alarmScheduler.editAlarm(alarm)
    }

    override suspend fun activeAlarm(alarm: AlarmModel, isActive: Boolean) {
        val updatedAlarm = alarm.copy(isOn = isActive)
        alarmList.value = alarmList.value?.map { if (it.id == alarm.id) updatedAlarm else it }

        appDAO.deleteAlarm(alarm.toAlarmEntity())
        appDAO.addAlarm(updatedAlarm.toAlarmEntity())

        if (isActive) alarmScheduler.scheduleAlarm(updatedAlarm)
        else alarmScheduler.cancelAlarm(updatedAlarm)
    }

    override fun getAlarmById(id: String): LiveData<AlarmModel?> {
        return MutableLiveData(alarmList.value?.find { it.id == id })
    }
}
