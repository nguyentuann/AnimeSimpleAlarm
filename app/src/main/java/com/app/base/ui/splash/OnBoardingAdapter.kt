package com.app.base.ui.splash

import com.app.base.data.model.OnBoardingModel
import com.app.base.databinding.OnboardingItemBinding
import com.brally.mobile.base.adapter.BaseListAdapter

class OnBoardingAdapter : BaseListAdapter<OnBoardingModel, OnboardingItemBinding>() {
    override fun bindData(
        binding: OnboardingItemBinding,
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