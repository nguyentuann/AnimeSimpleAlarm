package com.app.base

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.ui.setupWithNavController
import com.app.base.databinding.ActivityMainBinding
import com.app.base.helpers.PermissionHelper
import com.app.base.local.db.AppPreferences
import com.brally.mobile.base.activity.navigate
import com.brally.mobile.ui.features.main.BaseMainActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class MainActivity : BaseMainActivity<ActivityMainBinding, MainViewModel>() {

    private var lastBackPress = 0L

    override val viewModel: MainViewModel by viewModel()

    override val graphResId: Int
        get() = R.navigation.nav_graph

    override fun initView() {
        super.initView() // Gọi BaseMainActivity để inflate nav graph

        PermissionHelper.requestNotificationPermission(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val notificationManager = getSystemService(NotificationManager::class.java)
            if (!notificationManager.canUseFullScreenIntent()) {
                startActivity(Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT))
            }
        }

        // Thiết lập start destination động
        navController?.let { controller ->
            val navGraph = controller.navInflater.inflate(R.navigation.nav_graph)
            navGraph.setStartDestination(
                viewModel.getStartDestination()
            )
            controller.graph = navGraph

            setupBottomNavigation(controller)

            setupBackPressHandler(controller)

            if (!viewModel.isFirstLaunch()) {
                handleIntent(intent)
            }
        }
    }

    private fun setupBackPressHandler(navController: NavController) {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                @SuppressLint("RestrictedApi")
                override fun handleOnBackPressed() {

                    // Entry 0 = graph, entry 1 = fragment hiện tại
                    val backStack = navController.currentBackStack.value
                    val isRoot = backStack.size <= 2

                    if (!isRoot) {
                        navController.popBackStack()
                        return
                    }

                    val now = System.currentTimeMillis()

                    if (now - lastBackPress < 2000) {
                        // Thoát app
                        finish()
                    } else {
                        lastBackPress = now
                        Toast.makeText(
                            this@MainActivity,
                            R.string.exit,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        )
    }

    override fun attachBaseContext(newBase: Context) {
        val prefs = AppPreferences(newBase)
        val locale = Locale(prefs.appLanguage)

        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)

        val newContext = newBase.createConfigurationContext(config)
        super.attachBaseContext(newContext)
    }

    override fun initListener() {
    }

    override fun initData() {
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    override fun onFlowFinished() {
        viewModel.markFirstLaunchCompleted()
    }

    private fun setupBottomNavigation(navController: NavController) {
        val bottomNav = binding.bottomNav
        bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            bottomNav.isVisible = when (destination.id) {
                R.id.homeFragment,
                R.id.quickAlarmFragment,
                R.id.timerFragment,
                R.id.stopwatchFragment -> true

                else -> false
            }
        }

        bottomNav.setOnItemSelectedListener { item ->
            navController.currentDestination?.id?.let { currentId ->
                if (currentId == item.itemId) {
                    return@setOnItemSelectedListener true
                }
            }
            when (item.itemId) {
                R.id.homeFragment -> {
                    navigate(R.id.homeFragment)
                    true
                }

                R.id.quickAlarmFragment -> {
                    navigate(R.id.quickAlarmFragment)
                    true
                }

                R.id.timerFragment -> {
                    navigate(R.id.timerFragment)
                    true
                }

                R.id.stopwatchFragment -> {
                    navigate(R.id.stopwatchFragment)
                    true
                }

                else -> false
            }
        }
    }

    private fun handleIntent(intent: Intent?) {
        viewModel.getDestinationFromIntent(intent)?.let { destination ->
            navController?.navigate(destination)
            // Cập nhật BottomNavigationView tương ứng
            binding.bottomNav.selectedItemId = when (destination) {
                R.id.timerFragment -> R.id.timerFragment
                R.id.stopwatchFragment -> R.id.stopwatchFragment
                else -> binding.bottomNav.selectedItemId
            }
            // Xóa extra để tránh navigate lại
            viewModel.consumeIntent(intent)
        }
    }
}

