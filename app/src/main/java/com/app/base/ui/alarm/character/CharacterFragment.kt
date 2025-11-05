package com.app.base.ui.alarm.character

import androidx.recyclerview.widget.GridLayoutManager
import com.app.base.R
import com.app.base.databinding.FragmentCharacterBinding
import com.app.base.utils.AppConstants
import com.app.base.ui.alarm.NewAlarmViewModel
import com.language_onboard.ui.BaseFragment
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class CharacterFragment : BaseFragment<FragmentCharacterBinding>() {

    private val newAlarmViewModel by activityViewModel<NewAlarmViewModel>()


    private val characterAdapter: CharacterAdapter by lazy {
        CharacterAdapter(
            selectedCharacter = newAlarmViewModel.newAlarm.value?.character
                ?: R.drawable.img_naruto,
            selectCharacter = ::selectCharacter
        )
    }


    override fun getViewBinding(): FragmentCharacterBinding {
        return FragmentCharacterBinding.inflate(layoutInflater)
    }

    override fun initView() {
        setupToolbar()
        setupRecyclerView()
    }

    override fun getStatusBarColor() =
        requireContext().getColor(R.color.background)

    override fun getNavigationBarColor() =
        requireContext().getColor(R.color.background)



    private fun setupToolbar() = with(binding.characterToolbar) {
        tvToolbarTitle.setText(R.string.character)
        toolBar.setNavigationOnClickListener { back() }
        ivToolbarAction.setOnClickListener { back() }
    }

    private fun setupRecyclerView() = with(binding.recyclerCharacters) {
        layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = characterAdapter
        characterAdapter.submitList(AppConstants.getAllCharacters())
    }

    private fun selectCharacter(id: Int) {
        newAlarmViewModel.updateCharacter(id)
    }

    private fun back() = requireActivity().onBackPressedDispatcher.onBackPressed()
}