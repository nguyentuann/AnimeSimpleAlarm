package com.app.base.ui.root

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.base.ui.home.HomeFragment
import com.app.base.ui.quickalarm.QuickAlarmFragment
import com.app.base.ui.stopwatch.StopWatchFragment
import com.app.base.ui.timer.TimerFragment

class RootPagerAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    private val fragments = listOf(
        HomeFragment(),
        QuickAlarmFragment(),
        TimerFragment(),
        StopWatchFragment()
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}
