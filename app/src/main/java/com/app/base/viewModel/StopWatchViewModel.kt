package com.app.base.viewModel

import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.base.local.db.AppPreferences
import com.app.base.ui.stopwatch.StopWatchService
import com.app.base.utils.TimeConverter
import com.brally.mobile.base.viewmodel.BaseViewModel

class StopWatchViewModel(
    private val prefs: AppPreferences
) : BaseViewModel() {

    private val handler = Handler(Looper.getMainLooper())

    private val _elapsedMillis = MutableLiveData(0L)
    val elapsedMillis: LiveData<Long> = _elapsedMillis

    private val _isRunning = MutableLiveData(false)
    val isRunning: LiveData<Boolean> = _isRunning

    private var startTime = 0L

    private val _laps = MutableLiveData<List<String>>(emptyList())
    val laps: LiveData<List<String>> = _laps

    private val runnable = object : Runnable {
        override fun run() {
            _elapsedMillis.value = (_elapsedMillis.value ?: 0L) + 100
            handler.postDelayed(this, 100)
        }
    }

    private val updateRunnable = object : Runnable {
        override fun run() {
            _elapsedMillis.value = System.currentTimeMillis() - startTime
            handler.postDelayed(this, 100)
        }
    }

    fun start() {
        if (_isRunning.value == true) return

        val now = System.currentTimeMillis()

        // Nếu đang pause resume lại
        val oldElapsed = _elapsedMillis.value ?: 0L
        startTime = now - oldElapsed

        _isRunning.value = true
        handler.post(runnable)

        prefs.saveStopwatchState(startTime, true)

        startService(startTime)
    }

    fun pause() {
        handler.removeCallbacks(runnable)
        handler.removeCallbacks(updateRunnable)
        _isRunning.value = false
        stopService()
        prefs.clearStopwatch()
    }


    fun reset() {
        pause()
        _elapsedMillis.value = 0L
        _laps.value = emptyList()
    }

    fun addLap() {
        val currentLap = TimeConverter.stopWatchFormatTime(_elapsedMillis.value ?: 0L)
        val updated = _laps.value.orEmpty() + currentLap
        _laps.value = updated
    }

    fun restoreState() {
        val running = prefs.isStopwatchRunning()

        if (running) {
            startTime = prefs.getStartTime()
            _isRunning.value = true
            handler.post(updateRunnable)
        }
    }

    // todo HelperService of StopWatch

    private fun startService(startTimeMillis: Long) {
        val intent = Intent(context, StopWatchService::class.java).apply {
            putExtra("START_TIME", startTimeMillis)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            context.startForegroundService(intent)
        else
            context.startService(intent)
    }

    fun stopService() {
        val intent = Intent(context, StopWatchService::class.java)
        context.stopService(intent)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(runnable)
    }
}
