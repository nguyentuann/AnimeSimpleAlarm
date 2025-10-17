package com.app.base.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.base.MainActivity
import com.app.base.local.db.AppPreferences
import org.koin.android.ext.android.inject

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val appPrefs: AppPreferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        if (appPrefs.isFirstLaunch) {
            startActivity(Intent(this, OnboardingActivity::class.java))
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
        finish()
    }
}
