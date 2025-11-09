package com.app.base.ui.alarm.dates

import androidx.recyclerview.widget.LinearLayoutManager
import com.app.base.R
import com.app.base.databinding.FragmentCharacterBinding
import com.app.base.ui.home.NewAlarmViewModel
import com.brally.mobile.base.activity.BaseFragment
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class DatesFragment : BaseFragment<FragmentCharacterBinding, DatesViewModel>() {

    private val newAlarmViewModel by activityViewModel<NewAlarmViewModel>()

    private val datesAdapter by lazy {
        DatesAdapter(
            positions = viewModel.selectedDateOfWeek.value ?: emptyList(),
            selectDateOfWeek = viewModel::selectDateOfWeek
        )
    }

    override fun initView() {
        setupToolbar()
        setupRecyclerView()
        viewModel.loadDates(newAlarmViewModel.newAlarm.value?.datesOfWeek)
    }

    override fun initListener() {
        viewModel.selectedDateOfWeek.observe(viewLifecycleOwner) { dates ->
            newAlarmViewModel.updateDatesOfWeek(dates)
        }
    }

    override fun initData() {
        viewModel.datesOfWeek.observe(viewLifecycleOwner) { dates ->
            datesAdapter.submitList(dates)
        }
    }

    private fun setupToolbar() = with(binding.characterToolbar) {
        tvToolbarTitle.setText(R.string.Repeat)
        toolBar.setNavigationOnClickListener { back() }
        ivToolbarAction.setOnClickListener { back() }
    }

    private fun setupRecyclerView() = with(binding.recyclerCharacters) {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = datesAdapter
    }

    private fun back() = requireActivity().onBackPressedDispatcher.onBackPressed()
}
