package com.app.base.ui.splash

import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.app.base.R
import com.app.base.data.model.OnBoardingModel
import com.app.base.databinding.FragmentOnBoardingBinding
import com.app.base.local.db.AppPreferences
import com.language_onboard.ui.BaseFragment
import org.koin.android.ext.android.inject

class OnboardingFragment : BaseFragment<FragmentOnBoardingBinding>() {

    private val appPrefs: AppPreferences by inject()

    override fun getViewBinding(): FragmentOnBoardingBinding =
        FragmentOnBoardingBinding.inflate(layoutInflater)

    override fun initView() {
        val items = listOf(
            OnBoardingModel(
                R.drawable.onboarding1,
                "Chào mừng",
                "Ứng dụng báo thức thông minh giúp bạn quản lý thời gian hiệu quả."
            ),
            OnBoardingModel(
                R.drawable.onboarding2,
                "Tuỳ chỉnh dễ dàng",
                "Tạo và quản lý báo thức theo cách bạn muốn."
            ),
        )

        val adapter = OnBoardingAdapter().apply { submitList(items) }

        with(binding) {
            viewPager.adapter = adapter

            btnNext.setOnClickListener {
                if (viewPager.currentItem < items.size - 1) {
                    viewPager.currentItem += 1
                } else {
                    // Lưu là đã xem xong
                    appPrefs.isFirstLaunch = false
                    navigateToHome()
                }
            }

            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    btnNext.text = if (position == items.size - 1) "Bắt đầu ngay" else "Tiếp theo"
                }
            })
        }
    }

    override fun getStatusBarColor() =
        requireContext().getColor(R.color.background)

    override fun getNavigationBarColor() =
        requireContext().getColor(R.color.background)

    private fun navigateToHome() {
        findNavController().navigate(R.id.action_to_home)
    }
}
