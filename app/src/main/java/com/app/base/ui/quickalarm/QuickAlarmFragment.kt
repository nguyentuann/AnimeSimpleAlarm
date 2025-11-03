package com.app.base.ui.quickalarm

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.app.base.R
import com.app.base.components.CommonComponents
import com.app.base.data.model.AlarmModel
import com.app.base.databinding.FragmentQuickAlarmBinding
import com.app.base.utils.LogUtil
import com.app.base.viewModel.ListAlarmViewModel
import com.language_onboard.ui.BaseFragment
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.util.Calendar
import java.util.UUID

class QuickAlarmFragment : BaseFragment<FragmentQuickAlarmBinding>() {

    private var selectedMinutes: Int? = null
    private val listAlarmViewModel by activityViewModel<ListAlarmViewModel>()

    override fun getViewBinding() = FragmentQuickAlarmBinding.inflate(layoutInflater)

    override fun initView() {
        setupToolbar()
        setupQuickAlarmButtons()
        setupCreateOwnButton()
    }

    override fun getStatusBarColor() =
        requireContext().getColor(R.color.background)

    override fun getNavigationBarColor() =
        requireContext().getColor(R.color.background)

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
                selectedMinutes = minutes
                resetButtonColors()
                button.setBackgroundColor(requireContext().getColor(R.color.primary))
            }
        }
    }

    private fun saveAlarm() {
        val minutes = selectedMinutes
        if (minutes == null) {
            CommonComponents.toastText(
                requireContext(),
                getString(R.string.no_alarm_choosen)
            )
            return
        }

        val calendar = Calendar.getInstance().apply {
            add(Calendar.MINUTE, minutes)
        }

        val alarm = AlarmModel(
            id = UUID.randomUUID().toString(),
            hour = calendar.get(Calendar.HOUR_OF_DAY),
            minute = calendar.get(Calendar.MINUTE),
            isOn = true,
            message = "Quick Alarm",
            dateOfWeek = null,
            date = calendar.timeInMillis
        )

        LogUtil.log("Quick alarm to save: $alarm")
        listAlarmViewModel.saveAlarm(alarm)
        findNavController().navigate(R.id.action_to_home)
    }
}

