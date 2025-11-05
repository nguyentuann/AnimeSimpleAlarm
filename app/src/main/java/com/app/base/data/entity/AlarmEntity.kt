package com.app.base.data.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.base.data.model.AlarmModel
import com.app.base.helpers.AlarmHelper

@Entity(tableName = "alarms")
class AlarmEntity(
    @PrimaryKey
    val id: String,
    val hour: Int,
    val minute: Int,
    val isOn: Boolean,
    val message: String? = null,
    val sound: Int? = null,
    val dateOfWeek: Int? = null,
    val date: Long? = null,
    val character: Int? = null
)

fun AlarmEntity.toAlarmModel(): AlarmModel {
    return AlarmModel(
        id = id,
        hour = hour,
        minute = minute,
        isOn = isOn,
        message = message,
        sound = sound,
        dateOfWeek = if (dateOfWeek == null) null else AlarmHelper.fromBitToList(dateOfWeek),
        date = date,
        character = character
    )
}

fun AlarmModel.toAlarmEntity(): AlarmEntity {
    return AlarmEntity(
        id = id,
        hour = hour,
        minute = minute,
        isOn = isOn,
        message = message,
        sound = sound,
        dateOfWeek = if (dateOfWeek == null) null else AlarmHelper.fromListToBit(dateOfWeek!!),
        date = date,
        character = character
    )
}

fun AlarmEntity.toDataString(): String {
    return "AlarmEntity(id=$id, hour=$hour, minute=$minute, isOn=$isOn, message=$message, dateOfWeek=$dateOfWeek, date=$date)"
}