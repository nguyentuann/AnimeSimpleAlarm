package com.app.base.ui.alarm.sound

import androidx.recyclerview.widget.LinearLayoutManager
import com.app.base.R
import com.app.base.databinding.FragmentSoundBinding
import com.app.base.ui.home.NewAlarmViewModel
import com.brally.mobile.base.activity.BaseFragment
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class SoundFragment : BaseFragment<FragmentSoundBinding, SoundViewModel>() {

    private val newAlarmViewModel by activityViewModel<NewAlarmViewModel>()
    private val soundAdapter by lazy {
        SoundAdapter(
            selectSound = viewModel::selectSound
        )
    }

    override fun initView() {
        setupToolbar()
        setupRecyclerView()
        viewModel.loadSounds(newAlarmViewModel.newAlarm.value?.sound)
    }

    override fun initData() {
        viewModel.sounds.observe(viewLifecycleOwner) { sounds ->
            soundAdapter.submitList(sounds)
        }
    }

    override fun initListener() {
        viewModel.selectedSoundId.observe(viewLifecycleOwner) { sound ->
            newAlarmViewModel.updateSound(sound)
        }
    }

    private fun setupToolbar() = with(binding.characterToolbar) {
        tvToolbarTitle.setText(R.string.sound)
        toolBar.setNavigationOnClickListener { back() }
        ivToolbarAction.setOnClickListener { back() }
    }

    private fun setupRecyclerView() = with(binding.recyclerCharacters) {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = soundAdapter
    }


    private fun back() {
        requireActivity().onBackPressedDispatcher.onBackPressed()
        soundAdapter.release()
    }

}