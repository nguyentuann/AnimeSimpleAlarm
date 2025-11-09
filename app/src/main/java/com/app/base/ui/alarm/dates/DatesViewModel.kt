package com.app.base.ui.alarm.dates

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.base.utils.AppConstants
import com.brally.mobile.base.viewmodel.BaseViewModel

class DatesViewModel: BaseViewModel() {
    private val _datesOfWeek = MutableLiveData<List<Int>>()
    val datesOfWeek : LiveData<List<Int>> = _datesOfWeek

    private val _selectedDateOfWeek = MutableLiveData<List<Int>>()
    val selectedDateOfWeek: LiveData<List<Int>> = _selectedDateOfWeek

    fun loadDates(initialSelected: List<Int>? = null) {
        _datesOfWeek.value = AppConstants.getAllDates()
        _selectedDateOfWeek.value = initialSelected ?: emptyList()
    }

    fun selectDateOfWeek(dates: List<Int>) {
        _selectedDateOfWeek.value = dates
    }
}
