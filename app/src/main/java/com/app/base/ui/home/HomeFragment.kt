package com.app.base.ui.home

import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.base.R
import com.app.base.components.CommonComponents
import com.app.base.data.model.AlarmModel
import com.app.base.databinding.FragmentHomeBinding
import com.app.base.ui.alarm.AlarmAdapter
import com.brally.mobile.base.activity.BaseFragment
import com.brally.mobile.base.activity.navigate
import com.brally.mobile.base.activity.onBackPressed
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {

    private val listAlarmViewModel by activityViewModel<ListAlarmViewModel>()

    private val alarmAdapter by lazy {
        AlarmAdapter(
            { alarm -> deleteAlarm(alarm) },
            { alarm -> editAlarm(alarm) },
            { alarm -> enableAlarm(alarm) }
        )
    }

    override fun initView() {
        setupRecyclerView()
        setupObservers()
    }

    override fun initListener() {
        setupListeners()
    }

    override fun initData() {}

    private fun setupObservers() {
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
            navigate(R.id.newAlarmFragment)
        }
        floatBtnAdd.setOnClickListener {
            navigate(R.id.newAlarmFragment)
        }
        toolBar.toolBarSetting.setOnClickListener {
            navigate(R.id.settingFragment)
        }
    }

    private fun deleteAlarm(alarm: AlarmModel) {
        CommonComponents.confirmDialog(
            context = requireContext(),
            title = getString(R.string.delete_alarm),
            message = getString(R.string.delete_alarm_message),
            onConfirm = {
                listAlarmViewModel.delete(alarm)
            }
        )
    }

    private fun editAlarm(alarmId: String) {
        val bundle = bundleOf("alarm_id" to alarmId)
        navigate(R.id.newAlarmFragment, bundle)
    }

    private fun enableAlarm(alarm: AlarmModel) {
        listAlarmViewModel.active(alarm, alarm.isOn)
    }
}
