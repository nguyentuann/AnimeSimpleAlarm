package com.app.base.ui.alarm.dates

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.base.R
import com.app.base.databinding.FragmentCharacterBinding
import com.app.base.utils.AppConstants
import com.app.base.viewModel.NewAlarmViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class DatesFragment : Fragment() {
    private var _binding: FragmentCharacterBinding? = null
    private val binding get() = _binding!!
    private lateinit var datesAdapter: DatesAdapter
    private val newAlarmViewModel by activityViewModel<NewAlarmViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCharacterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()

        datesAdapter = DatesAdapter(
            selectDates = { dates -> selectDates(dates) }
        )

        binding.recyclerCharacters.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = datesAdapter
        }

        datesAdapter.submitList(
            AppConstants.getAllDates()
        )

    }

    private fun initListener() {
        binding.characterToolbar.tvToolbarTitle.setText(R.string.character)
        binding.characterToolbar.toolBar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.characterToolbar.ivToolbarAction.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

    }

    private fun selectDates(dates: List<Int>) {
        newAlarmViewModel.updateCharacter(id)
    }
}