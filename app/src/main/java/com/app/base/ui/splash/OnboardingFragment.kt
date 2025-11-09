package com.app.base.ui.splash

import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.app.base.R
import com.app.base.databinding.FragmentOnBoardingBinding
import com.brally.mobile.base.activity.BaseFragment

class OnboardingFragment : BaseFragment<FragmentOnBoardingBinding, SplashViewModel>() {

    private lateinit var adapter: OnBoardingAdapter

    override fun initView() {
        adapter = OnBoardingAdapter()
        binding.viewPager.adapter = adapter

        viewModel.items.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.currentPage.observe(viewLifecycleOwner) { position ->
            binding.viewPager.currentItem = position
            binding.btnNext.text = if (position == (viewModel.items.value?.size ?: 1) - 1)
                "Start now" else "Continue"
        }

        viewModel.navigateToHome.observe(viewLifecycleOwner) { navigate ->
            if (navigate == true) {
                findNavController().navigate(R.id.action_to_home)
                viewModel.doneNavigating()
            }
        }
    }

    override fun initListener() {
        binding.btnNext.setOnClickListener {
            viewModel.onNextClicked()
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.onPageChanged(position)
            }
        })
    }

    override fun initData() {}
}

