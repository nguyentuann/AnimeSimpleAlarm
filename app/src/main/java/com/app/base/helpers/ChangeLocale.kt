package com.app.base.helpers
import android.content.Context
import android.content.res.Configuration
import java.util.Locale

fun Context.setAppLocale(langCode: String): Context {
    val locale = Locale(langCode)
    Locale.setDefault(locale)

    val config = Configuration(resources.configuration)
    config.setLocale(locale)
    config.setLayoutDirection(locale)

    return createConfigurationContext(config)
}