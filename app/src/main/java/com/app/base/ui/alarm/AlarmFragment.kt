package com.app.base.ui.alarm

import android.os.Bundle
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.app.base.R
import com.app.base.components.CommonComponents
import com.app.base.data.model.toMyString
import com.app.base.databinding.FragmentAlarmBinding
import com.app.base.helpers.AlarmHelper
import com.app.base.utils.AppConstants
import com.app.base.utils.LogUtil
import com.app.base.utils.TimeConverter
import com.app.base.ui.home.ListAlarmViewModel
import com.app.base.ui.home.NewAlarmViewModel
import com.brally.mobile.base.activity.BaseFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.util.Calendar

class AlarmFragment : BaseFragment<FragmentAlarmBinding, AlarmViewModel>() {


    private val newAlarmViewModel by activityViewModel<NewAlarmViewModel>()
    private val listAlarmViewModel by activityViewModel<ListAlarmViewModel>()


    override fun initView() {
        val alarmId = arguments?.getString("alarm_id")

        if (alarmId == null) {
            LogUtil.log("ko id ")
            newAlarmViewModel.initNewAlarmIfNeeded()
            binding.tvTitle.setText(R.string.new_alarm)
        } else {
            LogUtil.log("co id ")
            binding.tvTitle.setText(R.string.edit_alarm)
            listAlarmViewModel.getAlarmById(alarmId).observe(viewLifecycleOwner) { alarm ->
                if (alarm != null) {
                    LogUtil.log("alarm khac null")
                    newAlarmViewModel.setAlarmOnce(alarm)
                } else {
                    LogUtil.log("alarm null")
                }
            }
        }

        setupToolbar()
        setupButtons()
        initObserver()
    }

    override fun initListener() {
        setupMessageWatcher()
    }

    override fun initData() {
    }

    private fun initObserver() {
        newAlarmViewModel.newAlarm.observe(viewLifecycleOwner) { alarm ->

            if (alarm == null) return@observe

            binding.tvTime.text = TimeConverter.convertTimeToString(alarm.hour, alarm.minute)

            if (alarm.date != null) {
                binding.scheduleAlarm.text = getString(
                    R.string.schedule_for,
                    TimeConverter.dayMonthYearFormatFromCalendar(alarm.date!!)
                )
            } else {
                binding.scheduleAlarm.text = getString(R.string.schedule)
            }

            if (alarm.datesOfWeek != null) {
                binding.dates.text =
                    TimeConverter.convertListDateToString(requireContext(), alarm.datesOfWeek!!)
            } else {
                binding.dates.text = getString(R.string.never)
            }

            if (alarm.character != null) {
                binding.btnCharacter.text = AppConstants.getNameCharacterById(alarm.character!!)
            } else {
                binding.btnCharacter.setText(R.string.character)
            }

            if (alarm.sound != null) {
                binding.btnSound.setText(AppConstants.getNameSoundById(alarm.sound!!))
            } else {
                binding.btnSound.setText(R.string.default_tone)
            }

            if (alarm.message != null) {
                if (binding.tfMessage.text.toString() != alarm.message) {
                    binding.tfMessage.setText(alarm.message)
                }
            }
        }
    }

    // todo các hàm tự định nghĩa

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

    private fun setupMessageWatcher() {
        // Watch thay đổi từ EditText để cập nhật ViewModel
        binding.tfMessage.doOnTextChanged { text, _, _, _ ->
            val currentMessage = newAlarmViewModel.newAlarm.value?.message ?: ""
            if (text.toString() != currentMessage) {
                newAlarmViewModel.updateMessage(text.toString())
            }
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
            // todo lưu giờ được chọn
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

            //todo update date
            newAlarmViewModel.updateDate(AlarmHelper.getCalendarFromDate(y, m, d))
            newAlarmViewModel.updateDatesOfWeek(null)
        }

        picker.show(parentFragmentManager, "DatePicker")
    }

    private fun saveAlarm() {

        var alarm = newAlarmViewModel.newAlarm.value!!
        LogUtil.log(alarm.toMyString())

        val alarmId = arguments?.getString("alarm_id")
        if (alarmId != null) {
            listAlarmViewModel.updateAlarm(alarm)
        } else {
            newAlarmViewModel.isNoDateChoose()
            alarm = newAlarmViewModel.newAlarm.value!!
            listAlarmViewModel.saveAlarm(alarm)
        }

        newAlarmViewModel.clearAlarm()
        back()
    }

    private fun checkDiscardChanges() {
        if (newAlarmViewModel.isChange() || binding.tfMessage.text.trim()
                .isNotEmpty()
        ) {
            CommonComponents.confirmDialog(
                requireContext(),
                getString(R.string.discard_change),
                getString(R.string.discard_change_message),
                onConfirm = {
                    back()
                    newAlarmViewModel.clearAlarm()
                }
            )
        } else {
            back()
            newAlarmViewModel.clearAlarm()
        }
    }

    private fun back() = requireActivity().onBackPressedDispatcher.onBackPressed()

}