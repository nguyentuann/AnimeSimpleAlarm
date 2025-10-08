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
}

