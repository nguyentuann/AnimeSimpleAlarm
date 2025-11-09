package com.app.base.ui.alarm.character

import androidx.recyclerview.widget.GridLayoutManager
import com.app.base.R
import com.app.base.databinding.FragmentCharacterBinding
import com.app.base.ui.home.NewAlarmViewModel
import com.brally.mobile.base.activity.BaseFragment
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class CharacterFragment : BaseFragment<FragmentCharacterBinding, CharacterViewModel>() {

    private val newAlarmViewModel by activityViewModel<NewAlarmViewModel>()

    private val characterAdapter: CharacterAdapter by lazy {
        CharacterAdapter(
            selectedCharacter = viewModel.selectedCharacter.value ?: R.drawable.img_naruto,
            selectCharacter = viewModel::updateSelectedCharacter
        )
    }

    override fun initView() {
        setupToolbar()
        setupRecyclerView()
        viewModel.loadCharacters(newAlarmViewModel.newAlarm.value?.character)
    }

    override fun initListener() {
        viewModel.selectedCharacter.observe(viewLifecycleOwner) { id ->
            newAlarmViewModel.updateCharacter(id)
        }
    }

    override fun initData() {
        viewModel.characters.observe(viewLifecycleOwner) { characters ->
            characterAdapter.submitList(characters)
        }
    }

    private fun setupToolbar() = with(binding.characterToolbar) {
        tvToolbarTitle.setText(R.string.character)
        toolBar.setNavigationOnClickListener { back() }
        ivToolbarAction.setOnClickListener { back() }
    }

    private fun setupRecyclerView() = with(binding.recyclerCharacters) {
        layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = characterAdapter
    }

    private fun back() = requireActivity().onBackPressedDispatcher.onBackPressed()
}