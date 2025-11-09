package com.app.base.ui.timer

import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.base.local.db.AppPreferences
import com.brally.mobile.base.viewmodel.BaseViewModel

class TimerViewModel: BaseViewModel() {

    private val prefs: AppPreferences by lazy {
        AppPreferences(context)
    }

    private var countDownTimer: CountDownTimer? = null
    private var remainingTime = 0L

    private val _timeLeft = MutableLiveData<Long>()
    val timeLeft: LiveData<Long> = _timeLeft

    private val _isRunning = MutableLiveData<Boolean>()
    val isRunning: LiveData<Boolean> = _isRunning

    fun startTimer(durationMillis: Long) {
        val endTime = System.currentTimeMillis() + durationMillis
        prefs.saveTimer(endTime, true)

        startCountdown(durationMillis)
        startService(durationMillis)
    }

    private fun startCountdown(durationMillis: Long) {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(durationMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished
                _timeLeft.value = millisUntilFinished
            }

            override fun onFinish() {
                _isRunning.value = false
                _timeLeft.value = 0
                prefs.clearTimer()
                stopService()
            }
        }.start()

        _isRunning.value = true
    }

    fun resumeIfRunning() {
        val endTime = prefs.getEndTime()
        if (prefs.isRunning() && endTime > System.currentTimeMillis()) {
            val remaining = endTime - System.currentTimeMillis()
            startCountdown(remaining)
            startService(remaining)
        } else {
            _isRunning.value = false
            _timeLeft.value = 0
        }
    }

    fun pauseTimer() {
        countDownTimer?.cancel()
        prefs.saveTimer(System.currentTimeMillis() + remainingTime, false)
        _isRunning.value = false
        stopService()
    }

    fun resetTimer() {
        countDownTimer?.cancel()
        prefs.clearTimer()
        _isRunning.value = false
        _timeLeft.value = 0
        stopService()
    }

    // todo TimerService helper methods
    private fun startService(durationMillis: Long) {
        val intent = Intent(context, TimerService::class.java).apply {
            putExtra("TIME_IN_MILLIS", durationMillis)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            context.startForegroundService(intent)
        else
            context.startService(intent)
    }

    fun stopService() {
        val intent = Intent(context, TimerService::class.java)
        context.stopService(intent)
    }
}