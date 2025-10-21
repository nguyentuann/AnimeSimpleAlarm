package com.app.base

import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.app.base.helpers.PermissionHelper
import com.app.base.helpers.setAppLocale
import com.app.base.local.db.AppPreferences
import com.app.base.utils.LogUtil
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    private val appPrefs: AppPreferences by inject()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PermissionHelper.requestNotificationPermission(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            if (!getSystemService(NotificationManager::class.java).canUseFullScreenIntent()) {
                startActivity(Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT))
            }
        }


        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        setAppLocale(appPrefs.appLanguage)

        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // 🔹 Kết nối với BottomNavigationView
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.newAlarmFragment,
                R.id.settingFragment,
                R.id.characterFragment,
                R.id.soundFragment,
                R.id.datesFragment
                    -> {
                    bottomNav.visibility = View.GONE
                }

                else -> {
                    bottomNav.visibility = View.VISIBLE
                }
            }
        }

        // 🔹 Nếu muốn xử lý logic tùy chọn menu
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }

                R.id.quickAlarmFragment -> {
                    navController.navigate(R.id.quickAlarmFragment)
                    true
                }

                R.id.timerFragment -> {
                    navController.navigate(R.id.timerFragment)
                    true
                }

                R.id.stopwatchFragment -> {
                    navController.navigate(R.id.stopwatchFragment)
                    true
                }

                else -> false
            }
        }
        checkOpenTimer(intent)
        checkOpenStopWatch(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        checkOpenTimer(intent)
        checkOpenStopWatch(intent)
    }

    private fun checkOpenTimer(intent: Intent?) {
        LogUtil.log("vao checkOpenTimer")

        if (intent?.getBooleanExtra("OPEN_TIMER", false) == true) {
            // todo xóa extra để tránh gọi lại nhiều lần
            intent.removeExtra("OPEN_TIMER")

            // todo điều hướng bằng NavController
            if (::navController.isInitialized) {
                navController.navigate(R.id.timerFragment)
            } else {
                // todo trường hợp hiếm: navController chưa kịp khởi tạo
                Handler(Looper.getMainLooper()).post {
                    val navHostFragment =
                        supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                    navHostFragment.navController.navigate(R.id.timerFragment)
                }
            }

            // todo cập nhật lại trạng thái chọn trên BottomNavigationView
            val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
            bottomNav.selectedItemId = R.id.timerFragment
        }
    }

    private fun checkOpenStopWatch(intent: Intent?) {
        LogUtil.log("vao checkOpenStopWatch")

        if (intent?.getBooleanExtra("OPEN_STOPWATCH", false) == true) {
            // todo xóa extra để tránh gọi lại nhiều lần
            intent.removeExtra("OPEN_STOPWATCH")

            // todo điều hướng bằng NavController
            if (::navController.isInitialized) {
                navController.navigate(R.id.stopwatchFragment)
            } else {
                // todo trường hợp hiếm: navController chưa kịp khởi tạo
                Handler(Looper.getMainLooper()).post {
                    val navHostFragment =
                        supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                    navHostFragment.navController.navigate(R.id.stopwatchFragment)
                }
            }

            // todo cập nhật lại trạng thái chọn trên BottomNavigationView
            val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
            bottomNav.selectedItemId = R.id.stopwatchFragment
        }
    }

}

