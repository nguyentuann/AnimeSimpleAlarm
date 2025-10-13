package com.app.base.viewModel

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.base.local.db.AppPreferences

class TimerViewModel(
    private val prefs: AppPreferences
) : ViewModel() {

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
            }
        }.start()

        _isRunning.value = true
    }

    fun resumeIfRunning() {
        val endTime = prefs.getEndTime()
        if (prefs.isRunning() && endTime > System.currentTimeMillis()) {
            val remaining = endTime - System.currentTimeMillis()
            startCountdown(remaining)
        } else {
            _isRunning.value = false
            _timeLeft.value = 0
        }
    }

    fun pauseTimer() {
        countDownTimer?.cancel()
        prefs.saveTimer(System.currentTimeMillis() + remainingTime, false)
        _isRunning.value = false
    }

    fun resetTimer() {
        countDownTimer?.cancel()
        prefs.clearTimer()
        _isRunning.value = false
        _timeLeft.value = 0
    }
}
