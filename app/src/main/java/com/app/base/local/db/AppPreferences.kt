package com.app.base.local.db

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.app.base.R

class AppPreferences(context: Context) {

    companion object {
        private const val PREFS_NAME = "app_settings"
        private const val LANG_KEY = "app_language"
        private const val APP_THEME = "app_theme"
        private const val APP_CHARACTER = "app_character"
        private const val KEY_END_TIME = "end_time"
        private const val TIMER_IS_RUNNING = "is_running"

        private const val KEY_START_TIME = "stopwatch_start_time"
        private const val STOPWATCH_IS_RUNNING = "stopwatch_is_running"

    }

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var appLanguage: String
        get() = prefs.getString(LANG_KEY, "en") ?: "en"
        set(value) {
            prefs.edit { putString(LANG_KEY, value) }
        }

    var appTheme: Int
        get() = prefs.getInt(APP_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        set(value) = prefs.edit { putInt(APP_THEME, value) }

    var appCharacter: Int
        get() = prefs.getInt(APP_CHARACTER, R.raw.doraemon)
        set(value) = prefs.edit { putInt(APP_CHARACTER, value) }

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

