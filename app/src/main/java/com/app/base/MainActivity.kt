package com.app.base

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import com.app.base.databinding.ActivityMainBinding
import com.app.base.helpers.ContextUtils
import com.app.base.helpers.PermissionHelper
import com.app.base.local.db.AppPreferences
import com.app.base.utils.LogUtil
import com.brally.mobile.ui.features.main.BaseMainActivity
import com.language_onboard.data.local.CommonAppSharePref
import com.language_onboard.data.model.Language
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale
import kotlin.getValue

class MainActivity : BaseMainActivity<ActivityMainBinding, MainViewModel>() {

    private val commonSharePref by inject<CommonAppSharePref>()

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

            setupBackPressHandler(controller)

            if (!viewModel.isFirstLaunch()) {
                handleIntent(intent)
            } else {
                LogUtil.log("First launch, skipping intent handling")
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

//    override fun attachBaseContext(newBase: Context) {
//        val prefs = AppPreferences(newBase)
//        val locale = Locale(prefs.appLanguage)
//
//        val config = Configuration(newBase.resources.configuration)
//        config.setLocale(locale)
//
//        val newContext = newBase.createConfigurationContext(config)
//        super.attachBaseContext(newContext)
//    }

    override fun attachBaseContext(context: Context) {
        val locale = commonSharePref.languageCode ?: Language.ENGLISH.countryCode
        val localeUpdatedContext: ContextWrapper =
            ContextUtils.updateLocale(context, Locale(locale))
        super.attachBaseContext(localeUpdatedContext)
    }

    override fun initListener() {}

    override fun initData() {
        viewModel.markFirstLaunchCompleted()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        LogUtil.log("MainActivity received intent: $intent")
        val destination = viewModel.getDestinationFromIntent(intent)
        destination?.let {
            LogUtil.log("MainActivity handling intent to navigate to: $it")
            // Chỉ cần đảm bảo MainHomeFragment đang hiện
            navController?.navigate(R.id.mainHomeFragment)
            // Truyền thông tin destination xuống fragment
            navController?.currentBackStackEntry?.savedStateHandle?.set("destination", it)
            viewModel.consumeIntent(intent)
        }
    }

    override fun onFlowFinished() {
    }
}

