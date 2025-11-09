package com.app.base.ui.quickalarm

import androidx.navigation.fragment.findNavController
import com.app.base.R
import com.app.base.components.CommonComponents
import com.app.base.databinding.FragmentQuickAlarmBinding
import com.app.base.ui.home.ListAlarmViewModel
import com.app.base.utils.LogUtil
import com.brally.mobile.base.activity.BaseFragment
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class QuickAlarmFragment : BaseFragment<FragmentQuickAlarmBinding, QuickAlarmViewModel>() {

    private val listAlarmViewModel by activityViewModel<ListAlarmViewModel>()

    override fun initView() {
        setupToolbar()
        setupQuickAlarmButtons()
        setupCreateOwnButton()
    }

    override fun initListener() {
    }

    override fun initData() {
    }

    private fun setupToolbar() = with(binding.quickToolbar) {
        toolBar.navigationIcon = null
        tvToolbarTitle.setText(R.string.quick_alarm)
        ivToolbarAction.setOnClickListener { saveAlarm() }
    }

    private fun setupCreateOwnButton() {
        binding.btnCreateMyOwn.setOnClickListener {
            findNavController().navigate(R.id.action_quick_to_newAlarm)
        }
    }

    private fun setupQuickAlarmButtons() {
        val buttonsWithTime = mapOf(
            binding.btn5m to 5,
            binding.btn15m to 15,
            binding.btn30m to 30,
            binding.btn45m to 45,
            binding.btn1h to 60,
            binding.btn6h to 360,
            binding.btn12h to 720,
            binding.btn18h to 1080,
            binding.btn24h to 1440,
        )

        fun resetButtonColors() {
            buttonsWithTime.keys.forEach { button ->
                button.setBackgroundColor(requireContext().getColor(R.color.light_surface))
            }
        }

        buttonsWithTime.forEach { (button, minutes) ->
            button.setOnClickListener {
                viewModel.selectMinutes(minutes)
                resetButtonColors()
                button.setBackgroundColor(requireContext().getColor(R.color.primary))
            }
        }
    }

    private fun saveAlarm() {
        val alarm = viewModel.createAlarm()

        if (alarm == null) {
            CommonComponents.toastText(requireContext(), getString(R.string.no_alarm_choosen))
            return
        }

        LogUtil.log("Quick alarm to save: $alarm")
        listAlarmViewModel.saveAlarm(alarm)
        findNavController().navigate(R.id.action_to_home)
    }
}

