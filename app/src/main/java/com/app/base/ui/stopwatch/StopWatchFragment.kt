package com.app.base.ui.stopwatch

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.base.databinding.FragmentStopWatchBinding
import com.app.base.R

class StopwatchFragment : Fragment() {

    private var _binding: FragmentStopWatchBinding? = null
    private val binding get() = _binding!!

    private var isRunning = false
    private var elapsedMillis: Long = 0
    private val laps = mutableListOf<String>()
    private lateinit var lapAdapter: LapListAdapter

    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            elapsedMillis += 100
            updateStopwatchText()
            handler.postDelayed(this, 100)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStopWatchBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.btnStartStopwatch.setOnClickListener {
            if (isRunning) {
                pauseStopwatch()
            } else {
                startStopwatch()
            }
        }

        updateStopwatchText()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lapAdapter = LapListAdapter(laps)
        binding.lapList.adapter = lapAdapter
        binding.lapList.layoutManager = LinearLayoutManager(requireContext())


        binding.btnLap.setOnClickListener {
            if (isRunning) {
                addLap()
            } else {
                resetStopwatch()
            }
        }

    }

    private fun startStopwatch() {
        handler.post(runnable)
        isRunning = true
        binding.btnStartStopwatch.text = getString(R.string.pause)
        updateLapButton()
    }

    private fun pauseStopwatch() {
        handler.removeCallbacks(runnable)
        isRunning = false
        binding.btnStartStopwatch.text = getString(R.string.start)
        updateLapButton()
    }

    @SuppressLint("DefaultLocale")
    private fun updateStopwatchText() {
        val hours = elapsedMillis / 1000 / 3600
        val minutes = (elapsedMillis / 1000 % 3600) / 60
        val seconds = (elapsedMillis / 1000 % 60)
        val millis = elapsedMillis % 1000
        binding.tvStopwatch.text =
            String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, millis)
    }

    private fun updateLapButton() {
        if (isRunning) {
            binding.btnLap.text = getString(R.string.lap)
            binding.btnLap.isEnabled = true
        } else {
            binding.btnLap.text = getString(R.string.reset)
            binding.btnLap.isEnabled = true
        }
    }

    @SuppressLint("DefaultLocale")
    private fun addLap() {
        val lapTime = String.format(
            "%02d:%02d:%02d.%03d",
            elapsedMillis / 1000 / 3600,
            (elapsedMillis / 1000 % 3600) / 60,
            (elapsedMillis / 1000 % 60),
            elapsedMillis % 1000
        )
        laps.add(lapTime)
        lapAdapter.notifyItemInserted(laps.size - 1)
        binding.lapList.scrollToPosition(laps.size)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun resetStopwatch() {
        elapsedMillis = 0
        updateStopwatchText()
        laps.clear()
        lapAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        handler.removeCallbacks(runnable) // tránh leak
    }
}
