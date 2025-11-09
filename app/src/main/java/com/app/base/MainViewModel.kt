package com.app.base

import android.content.Intent
import com.app.base.local.db.AppPreferences
import com.brally.mobile.base.viewmodel.BaseViewModel

class MainViewModel(
    private val appPrefs: AppPreferences
) : BaseViewModel() {

    fun getAppLanguage(): String = appPrefs.appLanguage

    fun isFirstLaunch(): Boolean = appPrefs.isFirstLaunch

    fun markFirstLaunchCompleted() {
        appPrefs.isFirstLaunch = false
    }

    fun getStartDestination(): Int =
        if (isFirstLaunch()) R.id.onboardingFragment else R.id.homeFragment

    fun getDestinationFromIntent(intent: Intent?): Int? = when {
        intent?.getBooleanExtra("OPEN_TIMER", false) == true -> R.id.timerFragment
        intent?.getBooleanExtra("OPEN_STOPWATCH", false) == true -> R.id.stopwatchFragment
        else -> R.id.homeFragment
    }

    fun consumeIntent(intent: Intent?) {
        intent?.removeExtra("OPEN_TIMER")
        intent?.removeExtra("OPEN_STOPWATCH")
    }
}
