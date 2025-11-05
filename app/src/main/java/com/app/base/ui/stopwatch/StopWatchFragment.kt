package com.app.base.ui.stopwatch

import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.base.R
import com.app.base.databinding.FragmentStopWatchBinding
import com.app.base.utils.TimeConverter
import com.app.base.ui.stopwatch.StopWatchViewModel
import com.language_onboard.ui.BaseFragment
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class StopWatchFragment : BaseFragment<FragmentStopWatchBinding>() {

    private val viewModel: StopWatchViewModel by activityViewModel() // Koin inject

    private lateinit var lapAdapter: LapListAdapter

    override fun getStatusBarColor() =
        requireContext().getColor(R.color.background)

    override fun getNavigationBarColor() =
        requireContext().getColor(R.color.background)

    override fun getViewBinding(): FragmentStopWatchBinding =
        FragmentStopWatchBinding.inflate(layoutInflater)

    override fun initView() {
        setUpToolbar()

        lapAdapter = LapListAdapter()
        binding.lapList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = lapAdapter
        }
        initListener()
        viewModel.restoreState()
    }

    override fun initObserver() {
        // Quan sát thời gian
        viewModel.elapsedMillis.observe(viewLifecycleOwner) { elapsed ->
            binding.tvStopwatch.text = TimeConverter.stopWatchFormatTime(elapsed)
        }

        // Quan sát trạng thái chạy/dừng
        viewModel.isRunning.observe(viewLifecycleOwner) { running ->
            binding.btnStartStopwatch.text =
                if (running) getString(R.string.pause) else getString(R.string.start)
            binding.btnLap.text =
                if (running) getString(R.string.lap) else getString(R.string.reset)
        }

        // Quan sát danh sách laps
        viewModel.laps.observe(viewLifecycleOwner) { laps ->
            lapAdapter.submitList(laps.toList())
            binding.lapList.scrollToPosition(0)
        }
    }

    fun initListener() {
        binding.btnStartStopwatch.setOnClickListener {
            if (viewModel.isRunning.value == true) viewModel.pause()
            else viewModel.start()
        }

        binding.btnLap.setOnClickListener {
            if (viewModel.isRunning.value == true) viewModel.addLap()
            else viewModel.reset()
        }
    }

    private fun setUpToolbar() = with(binding.stopwatchToolbar) {
        toolBar.navigationIcon = null
        ivToolbarAction.isVisible = false
        tvToolbarTitle.text = getString(R.string.stopwatch)
    }
}
