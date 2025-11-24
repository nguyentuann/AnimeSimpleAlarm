package com.app.base.ui.root

import androidx.core.view.get
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.app.base.R
import com.app.base.databinding.FragmentMainHomeBinding
import com.app.base.utils.LogUtil
import com.brally.mobile.base.activity.BaseFragment

class RootFragment :
    BaseFragment<FragmentMainHomeBinding, RootViewModel>() {

    override fun initView() {
        // Nếu cần adjust bottom nav với insets (notch / gesture)
        adjustInsetsForBottomNavigation(binding.bottomNav)

        // Set ViewPager2 adapter
        binding.viewPager.adapter = RootPagerAdapter(this)
        binding.viewPager.isUserInputEnabled = true
    }

    override fun initListener() {
        // BottomNavigation click
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> binding.viewPager.currentItem = 0
                R.id.quickAlarmFragment -> binding.viewPager.currentItem = 1
                R.id.timerFragment -> binding.viewPager.currentItem = 2
                R.id.stopwatchFragment -> binding.viewPager.currentItem = 3
            }
            true
        }

        // Đồng bộ ViewPager2 → BottomNavigation
        binding.viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    binding.bottomNav.menu[position].isChecked = true
                }
            })
    }

    override fun initData() {
        val destination =
            findNavController().currentBackStackEntry?.savedStateHandle?.get<Int>("destination")

        LogUtil.log("MainHomeFragment received destination: $destination")

        destination?.let {
            binding.viewPager.currentItem = when (it) {
                R.id.homeFragment -> 0
                R.id.quickAlarmFragment -> 1
                R.id.timerFragment -> 2
                R.id.stopwatchFragment -> 3
                else -> 0
            }
            // Xóa sau khi dùng
            findNavController().currentBackStackEntry?.savedStateHandle?.remove<Int>("destination")
        }
    }

}
