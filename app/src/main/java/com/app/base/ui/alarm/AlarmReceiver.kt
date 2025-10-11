package com.app.base.ui.alarm

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.app.base.R
import com.app.base.data.model.AlarmModel
import com.app.base.ui.alarm.sound.AlarmSoundService
import com.app.base.utils.LogUtil
import org.koin.java.KoinJavaComponent.getKoin

class AlarmReceiver() : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        LogUtil.log("Nhận alarm")

        val alarmScheduler: AlarmScheduler = getKoin().get()

        val id = intent.getStringExtra("ALARM_ID")
        val message = intent.getStringExtra("ALARM_MESSAGE")
        val sound = intent.getIntExtra("ALARM_SOUND", 0)
        val hour = intent.getIntExtra("ALARM_HOUR", 0)
        val minute = intent.getIntExtra("ALARM_MINUTE", 0)
        val days = intent.getIntArrayExtra("DAYS")?.toList()


        val serviceIntent = Intent(context, AlarmSoundService::class.java).apply {
            putExtras(intent)
        }
        ContextCompat.startForegroundService(context, serviceIntent)


        val activityIntent = Intent(context, AlarmReceiverActivity::class.java).apply {
            setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or
                        Intent.FLAG_ACTIVITY_NO_USER_ACTION
            )
            putExtras(intent)
        }


        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            id.hashCode(),
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "alarm_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Anime Alarm")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setFullScreenIntent(fullScreenPendingIntent, true) // 👈 quan trọng
            .build()

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            with(NotificationManagerCompat.from(context)) {
                notify(id.hashCode(), notification)
            }
        }


        if (!days.isNullOrEmpty()) {
            // todo lên lịch cho lần tiếp theo
            val nextAlarm = AlarmModel(
                id!!,
                hour,
                minute,
                true,
                message,
                sound,
                days
            )
            alarmScheduler.scheduleAlarm(nextAlarm)
        }
    }
}