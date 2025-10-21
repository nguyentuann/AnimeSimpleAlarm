package com.app.base.local.db

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

class AppPreferences(context: Context) {

    companion object {
        private const val PREFS_NAME = "app_settings"

        private const val IS_FIRST_LAUNCH = "is_first_launch"

        private const val LANG_KEY = "app_language"
        private const val KEY_END_TIME = "end_time"
        private const val TIMER_IS_RUNNING = "is_running"

        private const val KEY_START_TIME = "stopwatch_start_time"
        private const val STOPWATCH_IS_RUNNING = "stopwatch_is_running"

        private const val NOTIFICATION = "notification"

    }

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var appLanguage: String
        get() = prefs.getString(LANG_KEY, "en") ?: "en"
        set(value) {
            prefs.edit { putString(LANG_KEY, value) }
        }

    var isFirstLaunch: Boolean
        get() = prefs.getBoolean(IS_FIRST_LAUNCH, true)
        set(value) = prefs.edit { putBoolean(IS_FIRST_LAUNCH, value) }



    var hasNotificationPermission: Boolean
        get() = prefs.getBoolean(NOTIFICATION, false)
        set(value) = prefs.edit { putBoolean(NOTIFICATION, value) }


    fun saveTimer(endTime: Long, isRunning: Boolean) {
        prefs.edit {
            putLong(KEY_END_TIME, endTime)
                .putBoolean(TIMER_IS_RUNNING, isRunning)
        }
    }

    fun clearTimer() {
        prefs.edit {
            remove(KEY_END_TIME)
                .remove(TIMER_IS_RUNNING)
        }
    }

    fun getEndTime(): Long = prefs.getLong(KEY_END_TIME, 0L)
    fun isRunning(): Boolean = prefs.getBoolean(TIMER_IS_RUNNING, false)

    fun saveStopwatchState(startTime: Long, isRunning: Boolean) {
        prefs.edit {
            putLong(KEY_START_TIME, startTime)
            putBoolean(STOPWATCH_IS_RUNNING, isRunning)
        }
    }

    fun getStartTime(): Long = prefs.getLong(KEY_START_TIME, 0L)
    fun isStopwatchRunning(): Boolean = prefs.getBoolean(STOPWATCH_IS_RUNNING, false)

    fun clearStopwatch() {
        prefs.edit {
            remove(KEY_START_TIME)
            remove(STOPWATCH_IS_RUNNING)
        }
    }
}

