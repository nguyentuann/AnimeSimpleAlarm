package com.app.base.ui.stopwatch

import android.annotation.SuppressLint
import com.app.base.databinding.LapItemBinding
import com.brally.mobile.base.adapter.BaseListAdapter

class LapListAdapter : BaseListAdapter<String, LapItemBinding>() {

    @SuppressLint("SetTextI18n")
    override fun bindData(binding: LapItemBinding, item: String, position: Int) {
        binding.lapIndex.text = (position + 1).toString()
        binding.lapItem.text = item
    }
}