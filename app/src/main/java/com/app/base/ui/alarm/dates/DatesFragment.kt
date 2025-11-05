package com.app.base.ui.alarm.dates

import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.base.R
import com.app.base.databinding.FragmentCharacterBinding
import com.app.base.utils.AppConstants
import com.app.base.ui.alarm.NewAlarmViewModel
import com.language_onboard.ui.BaseFragment
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class DatesFragment : BaseFragment<FragmentCharacterBinding>() {

    private val newAlarmViewModel by activityViewModel<NewAlarmViewModel>()

    private val datesAdapter by lazy {
        DatesAdapter(
            positions = newAlarmViewModel.newAlarm.value?.dateOfWeek ?: emptyList(),
            selectDateOfWeek = ::onDateOfWeekSelected
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
        tvToolbarTitle.setText(R.string.Repeat)
        toolBar.setNavigationOnClickListener { back() }
        ivToolbarAction.setOnClickListener { back() }
    }

    private fun setupRecyclerView() = with(binding.recyclerCharacters) {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = datesAdapter
        datesAdapter.submitList(AppConstants.getAllDates())
    }

    private fun onDateOfWeekSelected(dates: List<Int>) {
        newAlarmViewModel.updateDateOfWeek(dates)
        newAlarmViewModel.updateDate(null)
    }

    private fun back() = requireActivity().onBackPressedDispatcher.onBackPressed()
}
