package com.app.base.local.dao


import com.app.base.data.entity.AlarmEntity
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface AppDAO {
    @Insert
    suspend fun addAlarm(newAlarm: AlarmEntity)

    @Delete
    suspend fun deleteAlarm(alarm: AlarmEntity)

    @Query("SELECT dateOfWeek FROM alarms WHERE id = :id")
    suspend fun getDateOfWeek(id: Int): Int

    @Query("SELECT * FROM alarms")
    suspend fun getAllAlarms(): List<AlarmEntity>

    @Update
    suspend fun updateAlarm(alarm: AlarmEntity)

    @Query("UPDATE alarms SET isOn = :isActive WHERE id = :id")
    suspend fun updateAlarmActiveState(id: String, isActive: Boolean)

}
