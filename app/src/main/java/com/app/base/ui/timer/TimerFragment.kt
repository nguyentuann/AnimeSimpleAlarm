package com.app.base.ui.timer

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.app.base.R
import com.app.base.databinding.FragmentTimerBinding

class TimerFragment : Fragment() {

    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!

    private var countDownTimer: CountDownTimer? = null
    private var timerMillis: Long = 5 * 60 * 1000 // mặc định 5 phút
    private var isRunning = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateTimerText(timerMillis)
        updateResetButtonState()

        val timeButtons = mapOf(
            binding.btn5 to 5,
            binding.btn10 to 10,
            binding.btn15 to 15,
            binding.btn30 to 30,
            binding.btn45 to 45
        )

        timeButtons.forEach { (button, minutes) ->
            button.setOnClickListener {
                timerMillis = minutes * 60 * 1000L
                updateTimerText(timerMillis)
                resetTimer()
            }
        }

        binding.btnStartTimer.setOnClickListener {
            if (isRunning) {
                pauseTimer()
            } else {
                updateTimeFromInput()
                startTimer()
            }
            updateResetButtonState()
        }

        binding.btnResetTimer.setOnClickListener {
            if (!isRunning) {
                timerMillis = 5 * 60 * 1000L
                updateTimerText(timerMillis)
                resetTimer()
            }
            updateResetButtonState()
        }

    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timerMillis, 100) {
            override fun onTick(millisUntilFinished: Long) {
                timerMillis = millisUntilFinished
                updateTimerText(timerMillis)
            }

            override fun onFinish() {
                isRunning = false
                binding.btnStartTimer.text = getString(R.string.start)
            }
        }.start()
        isRunning = true
        binding.btnStartTimer.text = getString(R.string.pause)
    }

    private fun pauseTimer() {
        countDownTimer?.cancel()
        isRunning = false
        binding.btnStartTimer.text = getString(R.string.start)
    }

    private fun resetTimer() {
        countDownTimer?.cancel()
        isRunning = false
    }

    @SuppressLint("DefaultLocale")
    private fun updateTimerText(millis: Long) {
        val hours = millis / 1000 / 3600
        val minutes = (millis / 1000 % 3600) / 60
        val seconds = millis / 1000 % 60

        // Giờ: chỉ hiển thị khi > 0, còn lại để trống
        if (hours > 0) {
            binding.etHours.setText(String.format("%02d", hours))
        } else {
            binding.etHours.setText("00")
        }

        // Luôn hiển thị phút và giây
        binding.etMinutes.setText(String.format("%02d", minutes))
        binding.etSeconds.setText(String.format("%02d", seconds))
    }

    private fun updateResetButtonState() {
        if (isRunning) {
            binding.btnResetTimer.isEnabled = false
            binding.btnResetTimer.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.surface))
        } else {
            binding.btnResetTimer.isEnabled = true
            binding.btnResetTimer.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.primary))
        }
    }

    private fun updateTimeFromInput() {
        val hours = binding.etHours.text.toString().toIntOrNull() ?: 0
        val minutes = binding.etMinutes.text.toString().toIntOrNull() ?: 0
        val seconds = binding.etSeconds.text.toString().toIntOrNull() ?: 0

        timerMillis = (hours * 3600 + minutes * 60 + seconds) * 1000L
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}