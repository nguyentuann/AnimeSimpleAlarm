package com.app.base.ui.settings

import android.graphics.drawable.Icon
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

            SettingModel(
                title = getString(R.string.feed_back),
                icon = Icon.createWithResource(requireContext(), R.drawable.ic_feedback),
                action = {
                    feedback()
                }
            ),
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

    private fun feedback() {
        CommonComponents.showRatingDialog(
            context = requireContext(),
            onSubmit = { rating, feedback ->
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
}