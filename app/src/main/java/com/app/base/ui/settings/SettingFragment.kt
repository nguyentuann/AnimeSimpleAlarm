package com.app.base.ui.settings

import android.graphics.drawable.Icon
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.base.R
import com.app.base.components.CommonComponents
import com.app.base.data.model.SettingModel
import com.app.base.databinding.FragmentSettingBinding
import com.app.base.helpers.setAppLocale
import com.app.base.local.db.AppPreferences
import com.language_onboard.ui.BaseFragment
import org.koin.android.ext.android.inject

class SettingFragment : BaseFragment<FragmentSettingBinding>() {

    private val appPrefs: AppPreferences by inject()
    private lateinit var settingAdapter: SettingAdapter

    private lateinit var settingList: List<SettingModel>

    override fun getStatusBarColor() =
        requireContext().getColor(R.color.background)

    override fun getNavigationBarColor() =
        requireContext().getColor(R.color.background)

    override fun getViewBinding(): FragmentSettingBinding {
        return FragmentSettingBinding.inflate(layoutInflater)
    }

    override fun initView() {
        super.initView()
        setUpToolbar()

        settingList = listOf(
            SettingModel(
                title = getString(R.string.language),
                icon = Icon.createWithResource(requireContext(), R.drawable.ic_language),
                action = { showLanguageDialog() }
            ),
//            SettingModel(
//                title = getString(R.string.theme),
//                icon = Icon.createWithResource(requireContext(), R.drawable.ic_theme),
//                action = { showThemeDialog() }
//            ),
            SettingModel(
                title = getString(R.string.policy),
                icon = Icon.createWithResource(requireContext(), R.drawable.ic_policy),
                action = {
                    developing()
                }
            ),
            SettingModel(
                title = getString(R.string.share_app),
                icon = Icon.createWithResource(requireContext(), R.drawable.ic_share),
                action = {
                    developing()
                }
            ),
            SettingModel(
                title = getString(R.string.feed_back),
                icon = Icon.createWithResource(requireContext(), R.drawable.ic_feedback),
                action = {
                    developing()
                }
            )
        )

        settingAdapter = SettingAdapter()
        binding.settingRecyclerView.apply {
            adapter = settingAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        settingAdapter.submitList(settingList)
    }

    private fun developing() {
        CommonComponents.toastText(
            context = requireContext(),
            message = getString(R.string.developing)
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
        val currentLang = appPrefs.appLanguage
        val selectedIndex = codes.indexOf(currentLang)

        CommonComponents.showSingleChoiceDialog(
            requireContext(),
            title = getString(R.string.language),
            options = languages,
            selectedIndex = selectedIndex.takeIf { it >= 0 } ?: 0,
            onSelected = { which ->
                val selectedCode = codes[which]
                requireContext().setAppLocale(selectedCode)
                appPrefs.appLanguage = selectedCode
                requireActivity().recreate()

                CommonComponents.toastText(
                    requireContext(),
                    getString(R.string.change_language)
                )
            },
            cancel = getString(R.string.cancel)
        )
    }

//    private fun showThemeDialog() {
//        val themes = arrayOf(
//            getString(R.string.light),
//            getString(R.string.dark),
//        )
//        val codes = arrayOf(
//            AppCompatDelegate.MODE_NIGHT_NO,        // Light
//            AppCompatDelegate.MODE_NIGHT_YES,       // Dark
//        )
//
//        val currentTheme = appPrefs.appTheme
//        val selectedIndex = codes.indexOf(currentTheme).takeIf { it >= 0 } ?: 2
//
//        CommonComponents.showSingleChoiceDialog(
//            requireContext(),
//            title = getString(R.string.theme),
//            options = themes,
//            selectedIndex = selectedIndex,
//            onSelected = { which ->
//                val newTheme = codes[which]
//                appPrefs.appTheme = newTheme
//                AppCompatDelegate.setDefaultNightMode(newTheme)
//                requireActivity().recreate()
//
//                CommonComponents.toastText(
//                    requireContext(),
//                    getString(R.string.change_theme)
//                )
//            },
//            cancel = getString(R.string.cancel)
//        )
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}