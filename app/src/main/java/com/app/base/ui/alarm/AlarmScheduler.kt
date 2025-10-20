package com.app.base.ui.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.app.base.data.model.AlarmModel
import com.app.base.helpers.AlarmHelper
import com.app.base.utils.LogUtil

class AlarmScheduler(
    private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // todo tạo pending intent để không bị nhầm id
    private fun getPendingIntent(alarm: AlarmModel): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("ALARM_ID", alarm.id)
            putExtra("ALARM_MESSAGE", alarm.message)
            putExtra("ALARM_SOUND", alarm.sound)
            putExtra("ALARM_HOUR", alarm.hour)
            putExtra("ALARM_MINUTE", alarm.minute)
            putExtra("DAYS", alarm.dateOfWeek?.toIntArray())
            putExtra("CHARACTER", alarm.character)
        }

        return PendingIntent.getBroadcast(
            context,
            alarm.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun scheduleAlarm(alarm: AlarmModel) {
        // todo schedule alarm with AlarmManager
        val triggerTime: Long = when {
            alarm.dateOfWeek != null && alarm.date == null -> {
                // Tìm ngày gần nhất
                AlarmHelper.getNextDayOfWeek(
                    alarm.hour,
                    alarm.minute,
                    alarm.dateOfWeek!!
                ).timeInMillis
            }

            alarm.date != null -> alarm.date!!
            else -> return
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            getPendingIntent(alarm)
        )

        LogUtil.log("Lên lịch cho id ${alarm.id}")

    }

    fun cancelAlarm(alarm: AlarmModel) {
        // todo cancel alarm with AlarmManager
        alarmManager.cancel(
            getPendingIntent(alarm)
        )
    }

    fun editAlarm(alarm: AlarmModel) {
        // todo change alarm with AlarmManager
        cancelAlarm(alarm)
        if (alarm.isOn) scheduleAlarm(alarm)
    }
}