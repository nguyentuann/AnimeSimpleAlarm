package com.app.base.ui.timer

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.app.base.R
import com.app.base.databinding.FragmentTimerBinding
import com.app.base.viewModel.TimerViewModel
import com.language_onboard.utils.visible
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class TimerFragment : Fragment() {

    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TimerViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("DefaultLocale")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolbar()
        setUpPickers()
        setUpQuickButtons()

        viewModel.timeLeft.observe(viewLifecycleOwner) { millis ->
            val h = TimeUnit.MILLISECONDS.toHours(millis)
            val m = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
            val s = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
            updatePickers(h.toInt(), m.toInt(), s.toInt())
        }

        viewModel.isRunning.observe(viewLifecycleOwner) { running ->
            binding.btnStartTimer.text =
                if (running) getString(R.string.pause) else getString(R.string.start)
            updateResetButtonState(running)
        }

        binding.btnStartTimer.setOnClickListener {
            val running = viewModel.isRunning.value ?: false
            if (running) {
                viewModel.pauseTimer()
            } else {
                val totalMillis = getPickerMillis()
                if (totalMillis > 0) {
                    viewModel.startTimer(totalMillis)
                }
            }
        }

        binding.btnResetTimer.setOnClickListener {
            viewModel.resetTimer()
            updatePickers(0, 0, 0)
        }

        viewModel.resumeIfRunning()
    }

    private fun getPickerMillis(): Long {
        val h = binding.pickerHours.value
        val m = binding.pickerMinutes.value
        val s = binding.pickerSeconds.value
        return ((h * 3600) + (m * 60) + s) * 1000L
    }

    private fun setUpToolbar() {
        binding.timerToolbar.toolBar.navigationIcon = null
        binding.timerToolbar.ivToolbarAction.isVisible = false
        binding.timerToolbar.tvToolbarTitle.setText(R.string.timer)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setUpPickers() {
        val formatter = android.widget.NumberPicker.Formatter { i -> String.format("%02d", i) }
        binding.pickerHours.apply {
            textSize = 60f;
            minValue = 0;
            maxValue = 23;
            setFormatter(formatter)
        }
        binding.pickerMinutes.apply {
            textSize = 60f;
            minValue = 0;
            maxValue = 59;
            setFormatter(formatter)
        }
        binding.pickerSeconds.apply {
            textSize = 60f;
            minValue = 0;
            maxValue = 59;
            setFormatter(formatter)
        }
    }

    private fun updatePickers(h: Int, m: Int, s: Int) {
        binding.pickerHours.value = h
        binding.pickerMinutes.value = m
        binding.pickerSeconds.value = s
    }

    private fun setUpQuickButtons() {
        val timeButtons = mapOf(
            binding.btn5 to 5,
            binding.btn10 to 10,
            binding.btn15 to 15,
            binding.btn30 to 30,
            binding.btn45 to 45
        )
        timeButtons.forEach { (button, minutes) ->
            button.setOnClickListener {
                val total = minutes * 60 * 1000L
                val h = (total / 1000 / 3600).toInt()
                val m = (total / 1000 / 60 % 60).toInt()
                val s = (total / 1000 % 60).toInt()
                updatePickers(h, m, s)
            }
        }
    }

    private fun updateResetButtonState(isRunning: Boolean) {
        binding.btnResetTimer.isEnabled = !isRunning
        val color = if (isRunning) R.color.surface else R.color.primary
        binding.btnResetTimer.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), color))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

