package com.app.base.ui.settings

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.app.base.components.CommonComponents
import com.app.base.databinding.FragmentSettingBinding
import com.app.base.local.db.AppPreferences
import com.app.base.R
import com.app.base.helpers.setAppLocale
import org.koin.android.ext.android.inject

class SettingFragment : Fragment() {

    private val appPrefs: AppPreferences by inject()

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Language selection
        binding.btnLanguage.setOnClickListener {
            showLanguageDialog()
        }

        binding.btnTheme.setOnClickListener {
            showThemeDialog()
        }

        binding.btnCharacter.setOnClickListener {
            showCharacterDialog()
        }

        binding.settingToolbar.toolBar.setNavigationOnClickListener {
            if (parentFragmentManager.backStackEntryCount > 0) {
                parentFragmentManager.popBackStack()
            } else {
                requireActivity().finish()
            }
        }

    }

    private fun showLanguageDialog() {
        val languages = arrayOf("English", "Tiếng Việt")
        val codes = arrayOf("en", "vi")
        val currentLang = appPrefs.appLanguage
        val selectedIndex = codes.indexOf(currentLang)

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.language)
            .setSingleChoiceItems(
                languages, selectedIndex
            ) { dialog, which ->
                requireContext().setAppLocale(codes[which])
                appPrefs.appLanguage = codes[which]
                requireActivity().recreate()
                dialog.dismiss()
                CommonComponents.toastText(
                    context = requireContext(),
                    message = getString(R.string.change_language)
                )
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun showThemeDialog() {
        val themes = arrayOf(
            getString(R.string.light),
            getString(R.string.dark),
        )
        val codes = arrayOf(
            AppCompatDelegate.MODE_NIGHT_NO,        // Light
            AppCompatDelegate.MODE_NIGHT_YES,       // Dark
        )

        val currentTheme = appPrefs.appTheme
        val selectedIndex = codes.indexOf(currentTheme).takeIf { it >= 0 } ?: 2

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.theme))
            .setSingleChoiceItems(
                themes, selectedIndex
            ) { dialog, which ->
                val newTheme = codes[which]
                appPrefs.appTheme = newTheme
                AppCompatDelegate.setDefaultNightMode(newTheme)
                requireActivity().recreate()
                dialog.dismiss()

                CommonComponents.toastText(
                    context = requireContext(),
                    message = getString(R.string.change_theme)
                )
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun showCharacterDialog() {
        // Map tên nhân vật -> file Lottie JSON (res/raw)
        val characters = mapOf(
            "Doraemon" to R.raw.doraemon,
            "Naruto" to R.raw.naruto,
            "Spideman" to R.raw.spiderman,
            "Superman" to R.raw.superman,
            "Chachan" to R.raw.chachan
        )

        val characterNames = characters.keys.toTypedArray()
        val currentCharacterRes = appPrefs.appCharacter
        val selectedIndex = characters.values.indexOf(currentCharacterRes)
            .takeIf { it >= 0 } ?: 0

        var tempSelectedRes = currentCharacterRes

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.character)
            .setSingleChoiceItems(characterNames, selectedIndex) { _, which ->
                val selectedCharacterName = characterNames[which]
                tempSelectedRes = characters[selectedCharacterName] ?: R.raw.doraemon

                showLottiePopup(tempSelectedRes)

            }
            .setPositiveButton("OK") { dialog, _ ->


                // Lưu resource ID vào appPrefs
                appPrefs.appCharacter = tempSelectedRes

                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showLottiePopup(lottieRes: Int) {
        val lottieDialog = Dialog(requireContext())
        lottieDialog.setContentView(R.layout.dialog_lottie) // layout chứa LottieAnimationView
        val lottieView = lottieDialog.findViewById<LottieAnimationView>(R.id.lottieView)
        lottieView.setAnimation(lottieRes)
        lottieView.loop(true)
        lottieView.playAnimation()

        lottieDialog.show()

        // Tự động đóng sau 3 giây
        lottieView.postDelayed({ lottieDialog.dismiss() }, 3000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}