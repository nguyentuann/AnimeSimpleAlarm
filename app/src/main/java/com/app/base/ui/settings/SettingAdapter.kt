package com.app.base.ui.settings

import com.app.base.data.model.SettingModel
import com.app.base.databinding.SettingItemBinding
import com.brally.mobile.base.adapter.BaseListAdapter


class SettingAdapter() : BaseListAdapter<SettingModel, SettingItemBinding>() {
    override fun bindData(
        binding: SettingItemBinding,
        item: SettingModel,
        position: Int
    ) {
        with(binding) {
            tvTitle.text  = binding.root.context.getString(item.titleRes)

            icLeft.setImageResource(item.iconRes)
            root.setOnClickListener {
                item.action()
            }
        }
    }

    override fun getItem(position: Int): SettingModel? {
        return super.getItem(position)
    }
}