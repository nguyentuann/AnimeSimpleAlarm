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

    @Query("SELECT dateOfWeek FROM alarm WHERE id = :id")
    suspend fun getDateOfWeek(id: Int): Int

    @Query("SELECT * FROM alarm")
    suspend fun getAllAlarms(): List<AlarmEntity>

    @Update
    suspend fun updateAlarm(alarm: AlarmEntity)

}
