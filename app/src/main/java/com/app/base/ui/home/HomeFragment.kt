package com.app.base.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.base.R
import com.app.base.components.CommonComponents
import com.app.base.data.model.AlarmModel
import com.app.base.databinding.FragmentHomeBinding
import com.app.base.ui.alarm.AlarmAdapter
import com.app.base.viewModel.ListAlarmViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class HomeFragment : Fragment() {
    private var _homeFragment: FragmentHomeBinding? = null
    private val homeFragment get() = _homeFragment!!

    private val listAlarmViewModel by activityViewModel<ListAlarmViewModel>()

    // todo giữ adapter như property để dùng lại
    private lateinit var alarmAdapter: AlarmAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _homeFragment = FragmentHomeBinding.inflate(inflater, container, false)
        return homeFragment.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // init adapter 1 lần
        alarmAdapter = AlarmAdapter(
            { alarm -> deleteAlarm(alarm) },
            { alarm -> editAlarm(alarm) },
            { alarm -> enableAlarm(alarm) }
        )
        initListener()


        homeFragment.alarmList.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = alarmAdapter
        }

        // quan sát dữ liệu
        listAlarmViewModel.alarmList.observe(viewLifecycleOwner) { alarms ->
            if (!alarms.isNullOrEmpty()) {
                alarmAdapter.submitList(alarms)
                homeFragment.alarmList.visibility = View.VISIBLE
                homeFragment.addAlarmCard.visibility = View.GONE
            } else {
                homeFragment.alarmList.visibility = View.GONE
                homeFragment.addAlarmCard.visibility = View.VISIBLE
            }
        }
    }

    private fun initListener() {
        homeFragment.addAlarmCard.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_newAlarm)
        }

        homeFragment.floatBtnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_newAlarm)
        }

        homeFragment.toolBar.toolBarSetting.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_setting)
        }

    }

    private fun deleteAlarm(alarm: AlarmModel) {
        CommonComponents.confirmDialog(
            requireContext(),
            getString(R.string.delete_alarm),
            getString(R.string.delete_alarm_message),
            onConfirm = {
                listAlarmViewModel.delete(alarm)
            },
        )
    }

    private fun editAlarm(alarmId: String) {
        // Sửa: truyền alarm_id
        val bundle = bundleOf("alarm_id" to alarmId)
        findNavController().navigate(R.id.action_home_to_newAlarm, bundle)
    }

    private fun enableAlarm(alarm: AlarmModel) {
        listAlarmViewModel.active(alarm, alarm.isOn)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _homeFragment = null
    }
}