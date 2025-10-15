package com.app.base.ui.splash

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.app.base.MainActivity
import com.app.base.R
import com.app.base.data.model.OnBoardingModel
import com.app.base.local.db.AppPreferences
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.android.ext.android.inject

class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var btnNext: Button
    private val appPrefs: AppPreferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_onboarding)

        viewPager = findViewById(R.id.viewPager)
        btnNext = findViewById(R.id.btnNext)

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

        val adapter = OnBoardingAdapter()
        adapter.submitList(items)


        viewPager.adapter = adapter

        btnNext.setOnClickListener {
            if (viewPager.currentItem < items.size - 1) {
                viewPager.currentItem += 1
            } else {
                // Lưu là đã xem xong
                appPrefs.isFirstLaunch = false
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        // Đổi text nút khi đến trang cuối
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                btnNext.text = if (position == items.size - 1) "Bắt đầu ngay" else "Tiếp theo"
            }
        })
    }
}