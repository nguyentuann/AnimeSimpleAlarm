package com.app.base.ui.quickalarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.app.base.R
import com.app.base.data.model.AlarmModel
import com.app.base.databinding.FragmentQuickAlarmBinding
import com.app.base.viewModel.ListAlarmViewModel
import java.util.Calendar
import java.util.UUID
import kotlin.random.Random

class QuickAlarmFragment : Fragment() {
    private var _binding: FragmentQuickAlarmBinding? = null
    private val binding get() = _binding!!

    private var selectedMinutes: Int? = null

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
        setUpToolbar()

        binding.btnCreateMyOwn.setOnClickListener {
            findNavController().navigate(R.id.action_quick_to_newAlarm)
        }

    }

    private fun setUpToolbar() {
        binding.quickToolbar.toolBar.navigationIcon = null
        binding.quickToolbar.tvToolbarTitle.setText(R.string.quick_alarm)
        binding.quickToolbar.ivToolbarAction.setOnClickListener {
            saveAlarm()
           findNavController().navigate(R.id.action_to_home)
        }
    }

    private fun saveAlarm() {
        val minutes = selectedMinutes
        if (minutes == null) {
            Toast.makeText(requireContext(), "Hãy chọn thời gian trước!", Toast.LENGTH_SHORT).show()
        } else {
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
        }
    }

    private fun setUpQuickAlarm() {

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
                button.setBackgroundColor(resources.getColor(R.color.surface))
            }
        }

        buttonsWithTime.forEach { (button, minutes) ->
            button.setOnClickListener {
                selectedMinutes = minutes
                resetButtonColors()
                button.setBackgroundColor(resources.getColor(R.color.primary))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
