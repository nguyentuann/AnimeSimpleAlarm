package com.app.base.ui.timer

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import com.app.base.MainActivity
import com.app.base.helpers.NotificationHelper
import com.app.base.utils.AppConstants
import java.util.concurrent.TimeUnit
import com.app.base.R

class TimerService : Service() {

    private var timer: CountDownTimer? = null
    private var remainingTime = 0L

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        remainingTime = intent?.getLongExtra("TIME_IN_MILLIS", 0L) ?: 0L

        // Tạo notification channel
        NotificationHelper.createChannels(
            this,
            AppConstants.TIMER_CHANNEL_ID,
            AppConstants.TIMER_NAME,
            "Channel for timer notifications"
        )

        // Gọi startForeground ngay lập tức để tránh crash
        val initialNotification = createNotification(remainingTime)
        startForeground(1, initialNotification)

        // Bắt đầu đếm ngược và cập nhật notification mỗi giây
        startCountdown()

        return START_STICKY
    }

    private fun startCountdown() {
        timer?.cancel()
        timer = object : CountDownTimer(remainingTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished
                updateNotification(millisUntilFinished)
            }

            override fun onFinish() {
                remainingTime = 0
                updateNotification(0)
                stopSelf()
            }
        }.start()
    }

    private fun updateNotification(millis: Long) {
        val notification = createNotification(millis)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1, notification)
    }

    private fun createNotification(millis: Long): Notification {
        val openIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("OPEN_TIMER", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationHelper.createNotificationBuilder(
            this,
            AppConstants.TIMER_CHANNEL_ID,
            pendingIntent,
            R.drawable.ic_timer,
            "Timer Running",
            "Time left: " + formatTime(millis)
        )
    }

    private fun formatTime(millis: Long): String {
        val h = TimeUnit.MILLISECONDS.toHours(millis)
        val m = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        val s = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
        return String.format("%02d:%02d:%02d", h, m, s)
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

