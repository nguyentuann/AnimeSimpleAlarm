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
import com.app.base.viewModel.ListAlarmViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.java.KoinJavaComponent.getKoin

class AlarmReceiverActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlarmReceiverBinding
    private var mediaPlayer: MediaPlayer? = null

    val alarmScheduler: AlarmScheduler = getKoin().get()
    private val listAlarmViewModel by viewModel<ListAlarmViewModel>()

    @SuppressLint("ImplicitSamInstance")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showOnLockScreen()
        enableEdgeToEdge()
        binding = ActivityAlarmReceiverBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getStringExtra("ALARM_ID")
        val message = intent.getStringExtra("ALARM_MESSAGE")
        val hour = intent.getIntExtra("ALARM_HOUR", 0)
        val minute = intent.getIntExtra("ALARM_MINUTE", 0)
        val character = intent.getIntExtra("CHARACTER", R.drawable.img_naruto)
        val sound = intent.getIntExtra("ALARM_SOUND", 0)
        val days = intent.getIntArrayExtra("DAYS")?.toList()


        binding.icon
            .setImageResource(IconHelper.getIconResourceForAlarm(hour))

        binding.time.text =
            TimeConverter.convertTimeToString(hour, minute)

        binding.message.text = message ?: "Alarm"


        setStopListener(
            setNextAlarm = {
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
                } else {
                    listAlarmViewModel.delete(
                        AlarmModel(
                            id!!,
                            hour,
                            minute,
                            true,
                            message,
                            sound,
                            days
                        )
                    )
                }
            }
        )

        binding.btnSnooze.setOnClickListener {
            var newHour = hour
            var newMinute = minute + 5

            if (newMinute >= 60) {
                newMinute %= 60
                newHour = (newHour + 1) % 24
            }
            val snoozeAlarm = AlarmModel(
                "snooze_$id",
                newHour,
                newMinute,
                true,
                message,
                sound,
                character = character,
                date = AlarmHelper.getNearestTime(newHour, newMinute),
                dateOfWeek = null
            )
            LogUtil.log("Đánh thức lại sau 5 phút vào $newHour:$newMinute với id snooze_$id")
            alarmScheduler.scheduleAlarm(snoozeAlarm)
            CommonComponents.toastText(
                this,
                getString(R.string.snooze_for_5_minutes)
            )
            stopService(Intent(this, AlarmSoundService::class.java))
            finish()
        }

        binding.bgImage.setImageResource(character)

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

    @SuppressLint("ImplicitSamInstance")
    fun setStopListener(setNextAlarm: () -> Unit = {}) {
        binding.btnStop.setOnClickListener {
            setNextAlarm()
            stopService(Intent(this, AlarmSoundService::class.java))
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}