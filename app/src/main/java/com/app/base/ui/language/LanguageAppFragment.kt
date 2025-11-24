package com.app.base.ui.language

import androidx.core.view.isVisible
import com.app.base.R
import com.app.base.databinding.FragmentLanguageAppBinding
import com.app.base.utils.LogUtil
import com.brally.mobile.base.activity.BaseFragment
import com.brally.mobile.base.activity.popBackStack
import com.brally.mobile.utils.RecyclerUtils
import com.brally.mobile.utils.singleClick
import com.language_onboard.data.local.CommonAppSharePref
import com.language_onboard.data.model.Language

class LanguageAppFragment : BaseFragment<FragmentLanguageAppBinding, LanguageViewModel>() {

    private val appSharePref: CommonAppSharePref by lazy {
        CommonAppSharePref(requireContext())
    }
    private val languageAdapter by lazy { LanguageAdapter() }
    private var isRefreshAds = true

    override fun initView() {
        adjustInsetsForBottomNavigation(binding.clContainer)
        setUpToolbar()
        RecyclerUtils.setLinearLayoutManager(
            requireActivity(),
            binding.rcvLanguage,
            languageAdapter
        )
        languageAdapter.setOnClickItemRecyclerView { language, _ ->
            if (isRefreshAds) {
                isRefreshAds = false
            }
            languageAdapter.setSelectLang(language)
            viewModel.mLanguageSelector = language
        }
    }

    override fun initListener() {
        binding.settingToolbar.ivToolbarAction.singleClick {
            val current = appSharePref.languageCode ?: Language.ENGLISH.countryCode
            val checked =
                languageAdapter.dataList.find { it.isCheck }?.language?.languageCode ?: "en"
            if (current == checked) {
                popBackStack()
                return@singleClick
            } else {
                updateNewLang().also {
                    LogUtil.log("Chạy vao updateNewLang")
                }
            }
        }
    }

    private fun updateNewLang() {
        viewModel.saveLang {
            activity?.let {
                it.recreate()
                popBackStack()
            }
        }
    }

    override fun initData() {
        viewModel.languageLiveData.observe { languageAdapter.addData(it) }
    }

    private fun setUpToolbar() = with(binding.settingToolbar) {
        toolBar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        ivToolbarAction.isVisible = true
        tvToolbarTitle.text = getString(R.string.settings)
    }
}
