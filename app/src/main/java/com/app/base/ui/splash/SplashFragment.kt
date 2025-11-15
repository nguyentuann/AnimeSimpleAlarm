package com.app.base.ui.splash

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.app.base.R
import com.app.base.databinding.FragmentSplashBinding
import com.brally.mobile.base.activity.navigate
import com.brally.mobile.ui.features.splash.BaseSplashFragment

class SplashFragment : BaseSplashFragment<FragmentSplashBinding, SplashViewModel>() {

    private val splashAdapter by lazy { SplashAdapter() }
    private var canNavigateHome = false

    override fun bannerView(): ViewGroup? {
        return binding.banner
    }

    override fun openHome() {
        if (!canNavigateHome) return
        navigate(R.id.homeFragment)
    }

    override fun initView() = with(binding) {
        viewPager.adapter = splashAdapter
        setupObservers()
    }

    override fun initListener() = with(binding) {
        btnNext.setOnClickListener {
            viewModel.onNextClicked()
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.onPageChanged(position)
            }
        })
    }

    private fun setupObservers() = with(viewModel) {
        items.observe(viewLifecycleOwner) { splashAdapter.submitList(it) }

        currentPage.observe(viewLifecycleOwner) { position ->
            binding.viewPager.currentItem = position
            val isLastPage = position == (items.value?.size ?: 1) - 1
            if (isLastPage) {
                canNavigateHome = true
            }
            binding.btnNext.text =
                if (isLastPage) getString(R.string.start) else getString(R.string.continue_text)
        }

        navigateToHome.observe(viewLifecycleOwner) {
            if (isAdded) openHome()
        }

        isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.isVisible = loading
        }
    }
}

