package com.app.base.ui.stopwatch

import android.annotation.SuppressLint
import com.app.base.databinding.LapItemBinding
import com.app.base.utils.randomColor
import com.brally.mobile.base.adapter.BaseListAdapter

class LapListAdapter : BaseListAdapter<String, LapItemBinding>() {

    @SuppressLint("SetTextI18n")
    override fun bindData(binding: LapItemBinding, item: String, position: Int) {
        val color = randomColor()
        binding.lapIndex.text = "Lap ${position + 1}"
        binding.lapIndex.setTextColor(color)
        binding.lapItem.text = item
        binding.lapItem.setTextColor(color)
    }
}