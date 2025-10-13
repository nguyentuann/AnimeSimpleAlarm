package com.app.base.ui.timer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.app.base.R


class TimerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Khi timer kết thúc, hiển thị thông báo
        val channelId = "timer_channel"
        val manager = context.getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(channelId, "Timer", NotificationManager.IMPORTANCE_HIGH)
        manager.createNotificationChannel(channel)

        val notification = Notification.Builder(context, channelId)
            .setContentTitle("Hết giờ rồi ⏰")
            .setContentText("Đồng hồ đếm ngược đã hoàn thành.")
            .setSmallIcon(R.drawable.ic_timer)
            .setAutoCancel(true)
            .build()

        manager.notify(1, notification)
    }
}
