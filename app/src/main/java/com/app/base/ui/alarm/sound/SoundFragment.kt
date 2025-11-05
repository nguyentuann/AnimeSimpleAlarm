package com.app.base.ui.alarm.sound

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.base.R
import com.app.base.databinding.FragmentSoundBinding
import com.app.base.utils.AppConstants
import com.app.base.ui.alarm.NewAlarmViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class SoundFragment : Fragment() {

    private var _binding: FragmentSoundBinding? = null
    private val binding get() = _binding!!

    private lateinit var soundAdapter: SoundAdapter
    private val newAlarmViewModel by activityViewModel<NewAlarmViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSoundBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()

        soundAdapter = SoundAdapter(
            selectSound = { id -> selectSound(id) }
        )

        binding.recyclerCharacters.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = soundAdapter
        }

        soundAdapter.submitList(
            AppConstants.getAllSoundIds()
        )

    }

    private fun initListener() {
        binding.characterToolbar.tvToolbarTitle.setText(R.string.sound)
        binding.characterToolbar.toolBar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.characterToolbar.ivToolbarAction.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

    }

    private fun selectSound(id: Int) {
        newAlarmViewModel.updateSound(id)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        soundAdapter.release()
        _binding = null
    }

}