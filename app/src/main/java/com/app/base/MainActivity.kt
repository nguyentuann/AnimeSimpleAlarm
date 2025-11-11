package com.app.base

import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.app.base.databinding.ActivityMainBinding
import com.app.base.helpers.PermissionHelper
import com.app.base.helpers.setAppLocale
import com.brally.mobile.base.activity.navigate
import com.brally.mobile.ui.features.main.BaseMainActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseMainActivity<ActivityMainBinding, MainViewModel>() {

    override val viewModel: MainViewModel by viewModel()

    override val graphResId: Int
        get() = R.navigation.nav_graph

    override fun initView() {
        super.initView() // Gọi BaseMainActivity để inflate nav graph

        PermissionHelper.requestNotificationPermission(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        setAppLocale(viewModel.getAppLanguage())



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

            if (!viewModel.isFirstLaunch()) {
                handleIntent(intent)
            }
        }
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
                R.id.newAlarmFragment,
                R.id.settingFragment,
                R.id.characterFragment,
                R.id.soundFragment,
                R.id.datesFragment,
                R.id.splashFragment -> false

                else -> true
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

