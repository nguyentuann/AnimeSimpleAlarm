package com.app.base.viewModel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.base.local.db.AppPreferences
import com.brally.mobile.base.viewmodel.BaseViewModel

class StopWatchViewModel(
    private val prefs: AppPreferences
) : BaseViewModel() {

    private val handler = Handler(Looper.getMainLooper())

    private val _elapsedMillis = MutableLiveData(0L)
    val elapsedMillis: LiveData<Long> = _elapsedMillis

    private val _isRunning = MutableLiveData(false)
    val isRunning: LiveData<Boolean> = _isRunning

    private val _laps = MutableLiveData<List<String>>(emptyList())
    val laps: LiveData<List<String>> = _laps

    private val runnable = object : Runnable {
        override fun run() {
            _elapsedMillis.value = (_elapsedMillis.value ?: 0L) + 100
            handler.postDelayed(this, 100)
        }
    }

    fun start() {
        if (_isRunning.value == true) return
        handler.post(runnable)
        _isRunning.value = true
    }

    fun pause() {
        handler.removeCallbacks(runnable)
        _isRunning.value = false
    }

    fun reset() {
        pause()
        _elapsedMillis.value = 0L
        _laps.value = emptyList()
    }

    fun addLap() {
        val currentLap = formatTime(_elapsedMillis.value ?: 0L)
        val updated = _laps.value.orEmpty() + currentLap
        _laps.value = updated
    }

    fun formatTime(millis: Long): String {
        val hours = millis / 1000 / 3600
        val minutes = (millis / 1000 % 3600) / 60
        val seconds = (millis / 1000 % 60)
        val ms = (millis % 1000) / 10
        return String.format("%02d:%02d:%02d.%02d", hours, minutes, seconds, ms)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(runnable)
    }
}
