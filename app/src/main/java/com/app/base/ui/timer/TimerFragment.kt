package com.app.base.ui.timer

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.widget.NumberPicker
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.app.base.R
import com.app.base.databinding.FragmentTimerBinding
import com.app.base.viewModel.TimerViewModel
import com.language_onboard.ui.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class TimerFragment : BaseFragment<FragmentTimerBinding>() {

    private val viewModel: TimerViewModel by viewModel()

    // Gom tất cả nút nhanh vào 1 map
    private val quickButtons by lazy {
        mapOf(
            binding.btn5 to 5,
            binding.btn10 to 10,
            binding.btn15 to 15,
            binding.btn30 to 30,
            binding.btn45 to 45
        )
    }

    override fun getStatusBarColor() =
        requireContext().getColor(R.color.background)

    override fun getNavigationBarColor() =
        requireContext().getColor(R.color.background)

    override fun getViewBinding(): FragmentTimerBinding {
        return FragmentTimerBinding.inflate(layoutInflater)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun initView() {
        super.initView()

        viewModel.resumeIfRunning()

        setUpToolbar()
        setUpPickers()
        setUpQuickButtons()

        binding.btnStartTimer.setOnClickListener {
            val running = viewModel.isRunning.value ?: false
            if (running) {
                viewModel.pauseTimer()
            } else {
                val totalMillis = getPickerMillis()
                if (totalMillis > 0) viewModel.startTimer(totalMillis)
            }
        }

        binding.btnResetTimer.setOnClickListener {
            viewModel.resetTimer()
            updatePickers(0, 5, 0)
        }
    }

    override fun initObserver() {
        super.initObserver()

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
            updateUIState(running)
        }

    }

    private fun getPickerMillis(): Long {
        val h = binding.pickerHours.value
        val m = binding.pickerMinutes.value
        val s = binding.pickerSeconds.value
        return ((h * 3600) + (m * 60) + s) * 1000L
    }

    private fun setUpToolbar() = with(binding.timerToolbar) {
        toolBar.navigationIcon = null
        ivToolbarAction.isVisible = false
        tvToolbarTitle.setText(R.string.timer)
    }

    @SuppressLint("DefaultLocale")
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setUpPickers() {
        val formatter = NumberPicker.Formatter { i -> String.format("%02d", i) }
        listOf(binding.pickerHours to 23, binding.pickerMinutes to 59, binding.pickerSeconds to 59)
            .forEach { (picker, max) ->
                picker.apply {
                    textSize = 100f
                    minValue = 0
                    maxValue = max
                    setFormatter(formatter)
                }
            }
    }

    private fun setUpQuickButtons() {
        quickButtons.forEach { (button, minutes) ->
            button.setOnClickListener {
                val total = minutes * 60 * 1000L
                val h = (total / 3600000).toInt()
                val m = (total / 60000 % 60).toInt()
                val s = (total / 1000 % 60).toInt()
                updatePickers(h, m, s)
            }
        }
    }

    private fun updatePickers(h: Int, m: Int, s: Int) {
        binding.pickerHours.value = h
        binding.pickerMinutes.value = m
        binding.pickerSeconds.value = s
    }

    private fun updateResetButtonState(isRunning: Boolean) {
        binding.btnResetTimer.isEnabled = !isRunning
        val color = if (isRunning) R.color.dark_surface else R.color.light_surface
        binding.btnResetTimer.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), color))
    }

    private fun updateUIState(isRunning: Boolean) {
        val pickers = listOf(
            binding.pickerHours,
            binding.pickerMinutes,
            binding.pickerSeconds
        )

        (pickers + quickButtons.keys).forEach { it.isEnabled = !isRunning }

        val color = if (isRunning) R.color.dark_surface else R.color.light_surface
        quickButtons.keys.forEach {
            it.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), color))
        }
    }

    override fun onPause() {
        super.onPause()
        if (viewModel.isRunning.value == true && (viewModel.timeLeft.value ?: 0L) > 0) {
            val intent = Intent(requireContext(), TimerService::class.java).apply {
                putExtra("TIME_IN_MILLIS", viewModel.timeLeft.value ?: 0L)
            }
            requireContext().startService(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        val intent = Intent(requireContext(), TimerService::class.java)
        requireContext().stopService(intent)
    }
}
