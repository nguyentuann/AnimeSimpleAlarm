package com.app.base.helpers
import android.content.Context
import android.content.res.Configuration
import com.app.base.utils.LogUtil
import java.util.Locale

fun Context.setAppLocale(langCode: String): Context {
    LogUtil.log("chạy vào setAppLocale với langCode: $langCode")
    val locale = Locale(langCode)
    Locale.setDefault(locale)

    val config = Configuration(resources.configuration)
    config.setLocale(locale)
    config.setLayoutDirection(locale)

    return createConfigurationContext(config)
}