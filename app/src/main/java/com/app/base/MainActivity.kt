package com.app.base
import android.Manifest
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.app.base.helpers.setAppLocale
import com.app.base.local.db.AppPreferences
import com.app.base.ui.alarm.NewAlarmFragment
import com.app.base.ui.alarm.QuickAlarmFragment
import vn.tutorial.simplealarmandroid.ui.home.HomeFragment
import com.app.base.ui.settings.SettingFragment
import com.app.base.ui.timer_stopwatch.TimerStopWatchFragment
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    private val appPrefs: AppPreferences by inject()

    lateinit var homeFragment: HomeFragment
    lateinit var newAlarmFragment: NewAlarmFragment
    lateinit var timerStopWatchFragment: TimerStopWatchFragment
    lateinit var settingFragment: SettingFragment
    lateinit var quickAlarmFragment: QuickAlarmFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            if (!getSystemService(NotificationManager::class.java).canUseFullScreenIntent()) {
                startActivity(Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT))
            }
        }


        AppCompatDelegate.setDefaultNightMode(appPrefs.appTheme)
        setAppLocale(appPrefs.appLanguage)

        setContentView(R.layout.activity_main)

        homeFragment = HomeFragment()
        newAlarmFragment = NewAlarmFragment()
        timerStopWatchFragment = TimerStopWatchFragment()
        settingFragment = SettingFragment()
        quickAlarmFragment = QuickAlarmFragment()

        changeFragment(homeFragment)
    }

    fun addNewAlarm() {
        changeFragment(newAlarmFragment)
    }

    fun timerStopWatch() {
        changeFragment(timerStopWatchFragment)
    }

    fun setting() {
        changeFragment(settingFragment)
    }

    fun quickAlarm() {
        changeFragment(quickAlarmFragment)
    }

    fun changeFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.home_container, fragment)

        if (addToBackStack) {
            transaction.addToBackStack(null)
        }

        transaction.commit()
    }

}

