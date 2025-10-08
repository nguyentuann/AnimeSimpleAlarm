package com.app.base.ui.alarm

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.app.base.R

class AlarmSoundService : Service() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.d("AlarmSoundService", "Service started")

        val sound = intent?.getIntExtra("ALARM_SOUND", 0) ?: 0
        val message = intent?.getStringExtra("ALARM_MESSAGE") ?: "Alarm"

        val soundUri = if (sound != 0) {
            "android.resource://${packageName}/$sound".toUri()
        } else {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        }

        mediaPlayer = MediaPlayer.create(this, soundUri).apply {
            isLooping = true
            start()
        }

        // foreground notification bắt buộc
        val notification = NotificationCompat.Builder(this, "alarm_channel")
            .setContentTitle("Alarm")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        startForeground(1, notification)

        return START_STICKY
    }

    override fun onDestroy() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
