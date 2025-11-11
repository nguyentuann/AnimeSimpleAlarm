package com.app.base.ui.splash

import com.app.base.data.model.OnBoardingModel
import com.app.base.databinding.SplashItemBinding
import com.brally.mobile.base.adapter.BaseListAdapter

class SplashAdapter : BaseListAdapter<OnBoardingModel, SplashItemBinding>() {
    override fun bindData(
        binding: SplashItemBinding,
        item: OnBoardingModel,
        position: Int
    ) {
        with(binding) {
            imgOnboarding.setImageResource(item.imageRes)
            tvTitle.text = item.title
            tvDesc.text = item.desc

        }
    }

    override fun getItem(position: Int): OnBoardingModel? {
        return super.getItem(position)
    }
}