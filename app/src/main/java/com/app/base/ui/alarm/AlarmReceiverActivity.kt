package com.app.base.ui.alarm

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.app.base.R
import com.app.base.components.CommonComponents
import com.app.base.data.model.AlarmModel
import com.app.base.databinding.ActivityAlarmReceiverBinding
import com.app.base.helpers.AlarmHelper
import com.app.base.helpers.IconHelper
import com.app.base.ui.alarm.sound.AlarmSoundService
import com.app.base.utils.LogUtil
import com.app.base.utils.TimeConverter
import com.app.base.ui.home.ListAlarmViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.java.KoinJavaComponent.getKoin

class AlarmReceiverActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlarmReceiverBinding
    private var mediaPlayer: MediaPlayer? = null

    private val alarmScheduler: AlarmScheduler = getKoin().get()
    private val listAlarmViewModel by viewModel<ListAlarmViewModel>()
    private var id: String? = null
    private var message: String? = null
    private var hour: Int = 0
    private var minute: Int = 0
    private var character: Int = 0
    private var sound: Int = 0
    private var days: List<Int>? = null

    @SuppressLint("ImplicitSamInstance")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showOnLockScreen()
        enableEdgeToEdge()
        binding = ActivityAlarmReceiverBinding.inflate(layoutInflater)
        setContentView(binding.root)

        id = intent.getStringExtra("ALARM_ID")
        message = intent.getStringExtra("ALARM_MESSAGE")
        hour = intent.getIntExtra("ALARM_HOUR", 0)
        minute = intent.getIntExtra("ALARM_MINUTE", 0)
        character = intent.getIntExtra("CHARACTER", R.drawable.img_naruto)
        sound = intent.getIntExtra("ALARM_SOUND", 0)
        days = intent.getIntArrayExtra("DAYS")?.toList()


        binding.icon
            .setImageResource(IconHelper.getIconResourceForAlarm(hour))

        binding.time.text =
            TimeConverter.convertTimeToString(hour, minute)

        binding.message.text = message ?: "Alarm"

        binding.bgImage.setImageResource(character)

        binding.btnStop.setOnClickListener {
            handleStopOrSnooze(false)
        }

        binding.btnSnooze.setOnClickListener {
            handleStopOrSnooze(true)
        }

    }

    // todo xử lý snooze & stop
    @SuppressLint("ImplicitSamInstance")
    private fun handleStopOrSnooze(isSnooze: Boolean = false) {
        if (isSnooze) {
            // 👉 Tính giờ báo lại sau 5 phút
            var newHour = hour
            var newMinute = minute + 5

            if (newMinute >= 60) {
                newMinute %= 60
                newHour = (newHour + 1) % 24
            }

            val snoozeAlarm = AlarmModel(
                id = "snooze_$id",
                hour = newHour,
                minute = newMinute,
                isOn = true,
                message = message,
                sound = sound,
                character = character,
                date = AlarmHelper.getNearestTime(newHour, newMinute),
                dateOfWeek = null
            )

            alarmScheduler.scheduleAlarm(snoozeAlarm)
            LogUtil.log("🔁 Báo lại sau 5 phút vào $newHour:$newMinute (id = snooze_$id)")

            CommonComponents.toastText(
                this,
                getString(R.string.snooze_for_5_minutes)
            )
        }

        val alarm = AlarmModel(
            id = id!!,
            hour = hour,
            minute = minute,
            isOn = true,
            message = message,
            sound = sound,
            character = character,
            date = null,
            dateOfWeek = days,
        )
        // 👉 Luôn đảm bảo lịch gốc được duy trì (nếu là alarm lặp)
        if (!days.isNullOrEmpty()) {
            alarmScheduler.scheduleAlarm(alarm)
            LogUtil.log("lên lịch mới vì days không null")
        } else {
            listAlarmViewModel.delete(alarm)
            LogUtil.log("xóa alarm vì không lặp lại")
        }

        // 👉 Dừng service & thoát màn hình
        stopService(Intent(this, AlarmSoundService::class.java))
        finish()
    }

    private fun showOnLockScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)

            val keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            )
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}