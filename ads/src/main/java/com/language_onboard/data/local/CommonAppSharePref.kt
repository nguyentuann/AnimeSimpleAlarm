package com.language_onboard.data.local

import android.content.Context
import android.util.Log
import java.util.Locale
import androidx.core.content.edit

class CommonAppSharePref(private val context: Context) {
    companion object {
        private const val PREF_SHOW_LANGUAGE = "pref_show_language"
        private const val PREF_LANGUAGE_CODE = "pref_language_code"
    }

    private val sharePref by lazy {
        context.getSharedPreferences("TrackingSharePref", Context.MODE_PRIVATE)
    }

    var isEnableLanguage: Boolean
        get() = sharePref.getBoolean(PREF_SHOW_LANGUAGE, false)
        set(value) {
            sharePref.edit { putBoolean(PREF_SHOW_LANGUAGE, value) }
        }

    var languageCode: String?
        get() = sharePref.getString(PREF_LANGUAGE_CODE, null)
        set(value) {
            sharePref.edit { putString(PREF_LANGUAGE_CODE, value) }
        }

    fun applyLanguage(languageCode: String) {
        Log.d("CommonAppSharePref", "Applying language: $languageCode")
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = context.resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        context.createConfigurationContext(config)
    }

}