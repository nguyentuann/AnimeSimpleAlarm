package com.app.base.ui.alarm

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.media.MediaPlayer
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.app.base.R
import com.app.base.components.CommonComponents
import com.app.base.data.model.AlarmModel
import com.app.base.databinding.FragmentNewAlarmBinding
import com.app.base.helpers.AlarmHelper
import com.app.base.utils.TimeConverter
import com.app.base.viewModel.ListAlarmViewModel
import com.app.base.viewModel.NewAlarmViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Calendar

class NewAlarmFragment : Fragment() {

    private var _binding: FragmentNewAlarmBinding? = null
    private val binding get() = _binding!!

    private val selectedDays = mutableSetOf<Int>()
    private val newAlarmViewModel by viewModel<NewAlarmViewModel>()
    private val listAlarmViewModel by activityViewModels<ListAlarmViewModel>()
    private var mediaPlayer: MediaPlayer? = null
    private var selectedIndex = -1

    // Map duy nhất để thao tác các ngày
    private val buttonsWithDays by lazy {
        mapOf(
            binding.btnSun to 1,
            binding.btnMon to 2,
            binding.btnTue to 3,
            binding.btnWed to 4,
            binding.btnThu to 5,
            binding.btnFri to 6,
            binding.btnSat to 7,
        )
    }

    private val soundList by lazy {
        listOf(
            getString(R.string.default_tone) to R.raw.base,
            getString(R.string.clock_tone) to R.raw.clockalarm,
            getString(R.string.ring_tone) to R.raw.ringtone,
            getString(R.string.school_tone) to R.raw.schoolbell,
            getString(R.string.trumpet_tone) to R.raw.terompetole
        )
    }

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

        binding.btnMusic.setOnClickListener {
            popUpSoundPicker()
        }

        buttonsWithDays.forEach { (button, dayOfWeek) ->
            button.setOnClickListener {
                val isSelected = selectedDays.toggle(dayOfWeek)
                updateDayButton(button, isSelected)
            }
        }



        binding.btnSave.setOnClickListener { saveAlarm() }
        binding.icSave.setOnClickListener { saveAlarm() }
    }

    private fun observeViewModel() {
        newAlarmViewModel.newAlarm.observe(viewLifecycleOwner) { alarm ->
            binding.tvTime.text = TimeConverter.convertTimeToString(alarm.hour, alarm.minute)
            binding.btnDays.visibility = if (alarm.date != null) View.GONE else View.VISIBLE
        }
    }

    private fun setupMessageWatcher() {
        binding.tfMessage.setText(newAlarmViewModel.newAlarm.value?.message ?: "")
        binding.tfMessage.doOnTextChanged { text, _, _, _ ->
            newAlarmViewModel.updateMessage(text.toString())
        }
    }

    private fun popUpSoundPicker() {
        val names = soundList.map { it.first }.toTypedArray()

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.select_tone)

        builder.setSingleChoiceItems(names, selectedIndex) { _, which ->
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null

            selectedIndex = which

            val soundRes = soundList[which].second
            mediaPlayer = MediaPlayer.create(requireContext(), soundRes)
            mediaPlayer?.apply {
                isLooping = false
                start()
            }
        }

        // Nút OK -> xác nhận
        builder.setPositiveButton("OK") { dialog, _ ->
            // Dừng nhạc thử, lưu nhạc được chọn
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null

            if (selectedIndex != -1) {
                val chosenSound = soundList[selectedIndex]

                // TODO: Lưu chosenSound.second làm nhạc chuông báo thức
                newAlarmViewModel.updateSound(chosenSound.second)
                binding.btnMusic.text = chosenSound.first
            }

            dialog.dismiss()
        }

        // Nút Cancel -> hủy
        builder.setNegativeButton(R.string.cancel) { dialog, _ ->
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            dialog.dismiss()
        }

        builder.show()
    }

    private fun popUpTimePicker() {
        val alarm = newAlarmViewModel.newAlarm.value
        val cal = Calendar.getInstance()
        val hour = alarm?.hour ?: cal.get(Calendar.HOUR_OF_DAY)
        val minute = alarm?.minute ?: cal.get(Calendar.MINUTE)

        TimePickerDialog(
            ContextThemeWrapper(requireContext(), R.style.TimePickerDialogStyle),
            { _, h, m -> newAlarmViewModel.updateTime(h, m) },
            hour, minute, true
        ).show()
    }

    private fun popUpDatePicker() {
        val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }

        val dp = DatePickerDialog(
            ContextThemeWrapper(requireContext(), R.style.TimePickerDialogStyle),
            { _, y, m, d ->
                binding.scheduleAlarm.text = getString(
                    R.string.schedule_for,
                    TimeConverter.dayMonthYearFormat(d, m, y)
                )
                newAlarmViewModel.updateDate(AlarmHelper.getCalendarFromDate(y, m, d))
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )

        dp.datePicker.minDate = cal.timeInMillis
        dp.setButton(DialogInterface.BUTTON_NEUTRAL, "Delete") { dialog, _ ->
            newAlarmViewModel.updateDate(null)
            binding.scheduleAlarm.setText(R.string.schedule_alarm)
            dialog.dismiss()
        }
        dp.show()
    }

    private fun updateDayButton(button: Button, selected: Boolean) {
        val color = if (selected) R.color.primary else R.color.secondary
        button.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), color))
    }

    private fun MutableSet<Int>.toggle(day: Int): Boolean {
        return if (contains(day)) {
            remove(day); false
        } else {
            add(day); true
        }
    }

    fun setAlarm(alarm: AlarmModel) {
        newAlarmViewModel.setAlarm(alarm)
        binding.tvTitle.setText(R.string.edit_alarm)
        binding.tfMessage.setText(alarm.message)
        binding.tvTime.text = TimeConverter.convertTimeToString(alarm.hour, alarm.minute)
        if (alarm.date != null) {
            binding.scheduleAlarm.text = TimeConverter.dayMonthYearFormatFromCalendar(alarm.date!!)
            binding.btnDays.visibility = View.GONE
        } else binding.btnDays.visibility = View.VISIBLE

        selectedDays.clear()
        alarm.dateOfWeek?.let { selectedDays.addAll(it) }
        buttonsWithDays.forEach { (button, day) ->
            updateDayButton(button, selectedDays.contains(day))
        }
    }

    private fun saveAlarm() {
        newAlarmViewModel.prepareAlarmBeforeSave(
            selectedDays,
            binding.tfMessage.text.toString(),
            soundList.getOrNull(selectedIndex)?.second ?: R.raw.base
        )
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
}