package com.app.base.ui.splash

import android.view.ViewGroup
import com.app.base.R
import com.app.base.databinding.FragmentSplashBinding
import com.brally.mobile.base.activity.navigate
import com.brally.mobile.ui.features.splash.BaseSplashFragment

class SplashFragment : BaseSplashFragment<FragmentSplashBinding, SplashViewModel>() {
    override fun bannerView(): ViewGroup? {
        return binding.banner
    }

    override fun openHome() {
        navigate(R.id.mainHomeFragment, isPop = true)
    }
}
