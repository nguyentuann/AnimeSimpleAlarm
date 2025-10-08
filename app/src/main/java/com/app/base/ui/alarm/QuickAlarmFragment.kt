package com.app.base.ui.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.app.base.MainActivity
import com.app.base.data.model.AlarmModel
import com.app.base.databinding.FragmentQuickAlarmBinding
import com.app.base.viewModel.ListAlarmViewModel
import java.util.Calendar
import java.util.UUID

class QuickAlarmFragment : Fragment() {
    private var _binding: FragmentQuickAlarmBinding? = null
    private val binding get() = _binding!!

    private val listAlarmViewModel by activityViewModels<ListAlarmViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentQuickAlarmBinding.inflate(inflater, container, false)
            .also { _binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpQuickAlarm()

        binding.btnCreateMyOwn.setOnClickListener {
            parentFragmentManager.popBackStack()
            (activity as? MainActivity)?.addNewAlarm()
        }

        binding.toolBar.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

    }

    private fun setUpQuickAlarm() {

        val buttonsWithTime = mapOf(
            binding.btn5m to 5,
            binding.btn15m to 15,
            binding.btn30m to 30,
            binding.btn45m to 45,
            binding.btn1h to 60,
            binding.btn2h to 120,
            binding.btn4h to 240,
            binding.btn8h to 480,
            binding.btn12h to 720,
            binding.btn24h to 1440,
        )
        buttonsWithTime.forEach { (button, minutes) ->
            button.setOnClickListener {

                val calendar = Calendar.getInstance()
                calendar.add(Calendar.MINUTE, minutes)

                val alarm = AlarmModel(
                    id = UUID.randomUUID().toString(),
                    hour = calendar.get(Calendar.HOUR_OF_DAY),
                    minute = calendar.get(Calendar.MINUTE),
                    isOn = true,
                    message = "Quick Alarm",
                    dateOfWeek = null,
                    date = calendar.timeInMillis,
                )
                listAlarmViewModel.saveAlarm(alarm)
                activity?.onBackPressedDispatcher?.onBackPressed()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}