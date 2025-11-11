package com.app.base.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.base.data.model.OnBoardingModel
import com.brally.mobile.base.viewmodel.BaseViewModel
import com.app.base.R
import com.app.base.local.db.AppPreferences

class SplashViewModel: BaseViewModel() {

    private val prefs: AppPreferences by lazy {
        AppPreferences(context)
    }

    private val _items = MutableLiveData<List<OnBoardingModel>>()
    val items: LiveData<List<OnBoardingModel>> = _items

    private val _currentPage = MutableLiveData(0)
    val currentPage: LiveData<Int> = _currentPage

    private val _navigateToHome = MutableLiveData<Boolean>()
    val navigateToHome: LiveData<Boolean> = _navigateToHome

    init {
        _items.value = listOf(
            OnBoardingModel(
                R.drawable.onboarding1,
                "Welcome to Anime Alarm",
                "This is the best alarm app for anime lovers."
            ),
            OnBoardingModel(
                R.drawable.onboarding2,
                "Easy customization",
                "Create and manage alarms the way you want."
            )
        )
    }

    fun onPageChanged(position: Int) {
        _currentPage.value = position
    }

    fun onNextClicked() {
        val list = _items.value ?: return
        val current = _currentPage.value ?: 0

        if (current < list.size - 1) {
            _currentPage.value = current + 1
        } else {
            prefs.isFirstLaunch = false
            _navigateToHome.value = true
        }
    }
}