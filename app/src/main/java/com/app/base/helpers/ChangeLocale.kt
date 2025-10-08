package com.app.base.helpers
import android.content.Context
import java.util.Locale

fun Context.setAppLocale(langCode: String) {
    val locale = Locale(langCode)
    Locale.setDefault(locale)
    val config = resources.configuration
    config.setLocale(locale)
    resources.updateConfiguration(config, resources.displayMetrics)
}
