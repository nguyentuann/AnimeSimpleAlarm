package com.app.base.repository

import androidx.lifecycle.LiveData
import com.app.base.data.model.AlarmModel

interface AlarmRepository {
    fun getAllAlarms(): LiveData<List<AlarmModel>>
    suspend fun addAlarm(alarm: AlarmModel)
    suspend fun deleteAlarm(alarm: AlarmModel)
    suspend fun updateAlarm(alarm: AlarmModel)
    suspend fun activeAlarm(alarm: AlarmModel, isActive: Boolean)
    fun getAlarmById(id: String): LiveData<AlarmModel?>
}