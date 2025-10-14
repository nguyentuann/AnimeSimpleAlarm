package com.app.base.ui.stopwatch

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.app.base.MainActivity
import com.app.base.R
import com.app.base.helpers.NotificationHelper
import com.app.base.utils.AppConstants
import com.app.base.utils.TimeConverter

class StopWatchService : Service() {

    companion object {
        const val NOTIFICATION_ID = 1001
    }

    private val handler = Handler(Looper.getMainLooper())
    private var startTime: Long = 0L

    private val runnable = object : Runnable {
        override fun run() {
            val now = System.currentTimeMillis()
            val elapsed = now - startTime
            updateNotification(elapsed)
            handler.postDelayed(this, 100)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startTime = intent?.getLongExtra("START_TIME", System.currentTimeMillis()) ?: System.currentTimeMillis()

        // Tạo channel (Android 8+)
        NotificationHelper.createChannels(
            this,
            AppConstants.STOPWATCH_CHANNEL_ID,
            AppConstants.STOPWATCH_NAME,
            "Channel for stopwatch notifications"
        )

        // Khởi tạo notification ban đầu
        val notification = createNotification(0L)
        startForeground(NOTIFICATION_ID, notification)

        // Bắt đầu chạy timer
        handler.post(runnable)

        return START_STICKY
    }

    private fun createNotification(elapsed: Long): Notification {
        val openIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("OPEN_STOPWATCH", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            100,
            openIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationHelper.createNotificationBuilder(
            this,
            AppConstants.STOPWATCH_CHANNEL_ID,
            pendingIntent,
            R.drawable.ic_stopwatch,
            "Stopwatch Running",
            TimeConverter.stopWatchFormatTime(elapsed)
        )
    }

    private fun updateNotification(elapsed: Long) {
        val notification = createNotification(elapsed)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }


    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)

        stopForeground(STOP_FOREGROUND_REMOVE)
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(NOTIFICATION_ID)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
