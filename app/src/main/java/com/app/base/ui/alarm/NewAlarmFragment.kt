package com.app.base.ui.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.app.base.R
import com.app.base.components.CommonComponents
import com.app.base.data.model.AlarmModel
import com.app.base.databinding.FragmentNewAlarmBinding
import com.app.base.helpers.AlarmHelper
import com.app.base.utils.TimeConverter
import com.app.base.viewModel.ListAlarmViewModel
import com.app.base.viewModel.NewAlarmViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.util.Calendar

class NewAlarmFragment : Fragment() {

    private var _binding: FragmentNewAlarmBinding? = null
    private val binding get() = _binding!!

    private val selectedDays = mutableSetOf<Int>()
    private val newAlarmViewModel by activityViewModel<NewAlarmViewModel>()
    private val listAlarmViewModel by activityViewModels<ListAlarmViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) =
        FragmentNewAlarmBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getString(ARG_ALARM_ID)?.let { id ->
            listAlarmViewModel.getAlarmById(id).value?.let { setAlarm(it) }
        }

        setupToolbar()
        setupButtons()
        observeViewModel()
        setupMessageWatcher()
        popUpTimePicker()
    }

    private fun setupToolbar() {
        binding.toolBarNewAlarm.setNavigationOnClickListener {
            checkDiscardChanges()
        }
    }

    private fun setupButtons() {
        binding.btnEdit.setOnClickListener {
            popUpTimePicker()
        }
        binding.btnCalendar.setOnClickListener { popUpDatePicker() }

        val bundle = Bundle()
        binding.btnSave.setOnClickListener { saveAlarm() }
        binding.icSave.setOnClickListener { saveAlarm() }

        binding.btnCharacter.setOnClickListener {
            findNavController().navigate(R.id.action_new_to_character, bundle)
        }

        binding.btnSound.setOnClickListener {
            findNavController().navigate(R.id.action_new_to_sound, bundle)
        }

        binding.btnDates.setOnClickListener {
            findNavController().navigate(R.id.action_new_to_dates, bundle)
        }
    }

    private fun observeViewModel() {
        newAlarmViewModel.newAlarm.observe(viewLifecycleOwner) { alarm ->
            binding.tvTime.text = TimeConverter.convertTimeToString(alarm.hour, alarm.minute)
        }
    }

    private fun setupMessageWatcher() {
        binding.tfMessage.setText(newAlarmViewModel.newAlarm.value?.message ?: "")
        binding.tfMessage.doOnTextChanged { text, _, _, _ ->
            newAlarmViewModel.updateMessage(text.toString())
        }
    }

    private fun popUpTimePicker() {
        val alarm = newAlarmViewModel.newAlarm.value
        val cal = Calendar.getInstance()
        val hour = alarm?.hour ?: cal.get(Calendar.HOUR_OF_DAY)
        val minute = alarm?.minute ?: cal.get(Calendar.MINUTE)

        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(hour)
            .setMinute(minute)
            .setTheme(R.style.TimePickerDialogStyle)
            .build()

        picker.addOnPositiveButtonClickListener {
            newAlarmViewModel.updateTime(picker.hour, picker.minute)
        }

        picker.show(parentFragmentManager, "TimePicker")
    }

    private fun popUpDatePicker() {
        val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }

        val tomorrow = MaterialDatePicker.todayInUtcMilliseconds() + 24 * 60 * 60 * 1000

        val constraints = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.from(tomorrow))
            .build()

        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date")
            .setSelection(cal.timeInMillis)
            .setTheme(R.style.DatePickerDialogStyle)
            .setCalendarConstraints(constraints)
            .build()

        picker.addOnPositiveButtonClickListener { selection ->
            val pickedCal = Calendar.getInstance().apply { timeInMillis = selection }
            val y = pickedCal.get(Calendar.YEAR)
            val m = pickedCal.get(Calendar.MONTH)
            val d = pickedCal.get(Calendar.DAY_OF_MONTH)

            binding.scheduleAlarm.text = getString(
                R.string.schedule_for,
                TimeConverter.dayMonthYearFormat(d, m, y)
            )
            newAlarmViewModel.updateDate(AlarmHelper.getCalendarFromDate(y, m, d))
        }

        picker.show(parentFragmentManager, "DatePicker")
    }

    fun setAlarm(alarm: AlarmModel) {
        newAlarmViewModel.setAlarm(alarm)
        binding.tvTitle.setText(R.string.edit_alarm)
        binding.tfMessage.setText(alarm.message)
        binding.tvTime.text = TimeConverter.convertTimeToString(alarm.hour, alarm.minute)
        if (alarm.date != null) {
            binding.scheduleAlarm.text = TimeConverter.dayMonthYearFormatFromCalendar(alarm.date!!)
        }

        selectedDays.clear()
        alarm.dateOfWeek?.let { selectedDays.addAll(it) }
    }

    private fun saveAlarm() {
        val alarm = newAlarmViewModel.newAlarm.value!!

        val alarmId = arguments?.getString("alarm_id")
        if (alarmId != null) {
            listAlarmViewModel.updateAlarm(alarm)
        } else {
            listAlarmViewModel.saveAlarm(alarm)
        }

        activity?.onBackPressedDispatcher?.onBackPressed()
    }

    private fun checkDiscardChanges() {
        if (newAlarmViewModel.isChange() || selectedDays.isNotEmpty() || binding.tfMessage.text.trim()
                .isNotEmpty()
        ) {
            CommonComponents.confirmDialog(
                requireContext(),
                getString(R.string.discard_change),
                getString(R.string.discard_change_message),
                onConfirm = { activity?.onBackPressedDispatcher?.onBackPressed() }
            )
        } else activity?.onBackPressedDispatcher?.onBackPressed()
    }

    companion object {
        private const val ARG_ALARM_ID = "alarm_id"

        fun newInstance(alarmId: String) = NewAlarmFragment().apply {
            arguments = Bundle().apply { putString(ARG_ALARM_ID, alarmId) }
        }
    }

    override fun onStop() {
        super.onStop()
        newAlarmViewModel.resetAlarm()
        selectedDays.clear()
        binding.tfMessage.setText("")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}