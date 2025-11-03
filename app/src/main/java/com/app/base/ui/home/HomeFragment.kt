package com.app.base.ui.home

import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.base.R
import com.app.base.components.CommonComponents
import com.app.base.data.model.AlarmModel
import com.app.base.databinding.FragmentHomeBinding
import com.app.base.ui.alarm.AlarmAdapter
import com.app.base.viewModel.ListAlarmViewModel
import com.language_onboard.ui.BaseFragment
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private val listAlarmViewModel by activityViewModel<ListAlarmViewModel>()

    private val alarmAdapter by lazy {
        AlarmAdapter(
            { alarm -> deleteAlarm(alarm) },
            { alarm -> editAlarm(alarm) },
            { alarm -> enableAlarm(alarm) }
        )
    }

    override fun getViewBinding(): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(layoutInflater)
    }

    override fun initView() {
        setupRecyclerView()
        setupListeners()
    }

    override fun initObserver() {
        listAlarmViewModel.alarmList.observe(viewLifecycleOwner) { alarms ->
            if (alarms.isNullOrEmpty()) {
                binding.alarmList.isVisible = false
                binding.addAlarmCard.isVisible = true
            } else {
                binding.alarmList.isVisible = true
                binding.addAlarmCard.isVisible = false
                alarmAdapter.submitList(alarms)
            }
        }
    }

    private fun setupRecyclerView() = with(binding.alarmList) {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = alarmAdapter
    }

    private fun setupListeners() = with(binding) {
        addAlarmCard.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_newAlarm)
        }
        floatBtnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_newAlarm)
        }
        toolBar.toolBarSetting.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_setting)
        }
    }

    private fun deleteAlarm(alarm: AlarmModel) {
        CommonComponents.confirmDialog(
            context = requireContext(),
            title = getString(R.string.delete_alarm),
            message = getString(R.string.delete_alarm_message),
            onConfirm = { listAlarmViewModel.delete(alarm) }
        )
    }

    private fun editAlarm(alarmId: String) {
        val bundle = bundleOf("alarm_id" to alarmId)
        findNavController().navigate(R.id.action_home_to_newAlarm, bundle)
    }

    private fun enableAlarm(alarm: AlarmModel) {
        listAlarmViewModel.active(alarm, alarm.isOn)
    }

    override fun getStatusBarColor() =
        requireContext().getColor(R.color.background)

    override fun getNavigationBarColor() =
        requireContext().getColor(R.color.background)
}
