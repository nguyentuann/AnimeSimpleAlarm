package com.app.base.ui.settings

import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.base.R
import com.app.base.components.CommonComponents
import com.app.base.databinding.FragmentSettingBinding
import com.app.base.helpers.setAppLocale
import com.brally.mobile.base.activity.BaseFragment

class SettingFragment : BaseFragment<FragmentSettingBinding, SettingViewModel>() {

    private lateinit var settingAdapter: SettingAdapter

    override fun initView() {
        setUpToolbar()

        settingAdapter = SettingAdapter()
        binding.settingRecyclerView.apply {
            adapter = settingAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        viewModel.loadSettings()
    }

    override fun initListener() {
        observeSettings()
        observeEvents()
    }

    override fun initData() {}

    private fun observeSettings() {
        viewModel.settingList.observe(viewLifecycleOwner) { list ->
            settingAdapter.submitList(list)
        }
    }

    private fun observeEvents() {
        viewModel.events.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { handleEvent(it) }
        }
    }

    private fun handleEvent(event: SettingViewModel.SettingEvent) {
        when (event) {
            SettingViewModel.SettingEvent.ShowLanguageDialog -> showLanguageDialog()
            SettingViewModel.SettingEvent.ShowFeedbackDialog -> feedback()
            SettingViewModel.SettingEvent.ShowDevelopingToast -> developing()
        }
    }

    private fun developing() {
        CommonComponents.toastText(
            context = requireContext(),
            message = getString(R.string.developing)
        )
    }

    private fun feedback() {
        CommonComponents.showRatingDialog(
            context = requireContext(),
            onSubmit = { _, _ ->
                CommonComponents.toastText(
                    requireContext(),
                    getString(R.string.thank_feedback)
                )
            }
        )
    }

    private fun setUpToolbar() = with(binding.settingToolbar) {
        toolBar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        ivToolbarAction.isVisible = false
        tvToolbarTitle.text = getString(R.string.settings)
    }

    private fun showLanguageDialog() {
        val languages = arrayOf("English", "Tiếng Việt")
        val codes = arrayOf("en", "vi")
        val currentLang = viewModel.prefs.appLanguage
        val selectedIndex = codes.indexOf(currentLang).takeIf { it >= 0 } ?: 0

        CommonComponents.showSingleChoiceDialog(
            requireContext(),
            title = getString(R.string.language),
            options = languages,
            selectedIndex = selectedIndex,
            onSelected = { which ->
                val selectedCode = codes[which]
                requireContext().setAppLocale(selectedCode)
                viewModel.prefs.appLanguage = selectedCode
                requireActivity().recreate()

                CommonComponents.toastText(
                    requireContext(),
                    getString(R.string.change_language)
                )
            },
            cancel = getString(R.string.cancel)
        )
    }
}