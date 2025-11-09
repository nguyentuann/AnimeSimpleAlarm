package com.app.base.ui.quickalarm

import androidx.lifecycle.MutableLiveData
import com.app.base.data.model.AlarmModel
import com.brally.mobile.base.viewmodel.BaseViewModel
import java.util.Calendar
import java.util.UUID

class QuickAlarmViewModel : BaseViewModel() {

    // Chọn số phút cho quick alarm
    private val _selectedMinutes = MutableLiveData<Int?>()

    fun selectMinutes(minutes: Int) {
        _selectedMinutes.value = minutes
    }

    // Tạo AlarmModel dựa trên phút đã chọn
    fun createAlarm(): AlarmModel? {
        val minutes = _selectedMinutes.value ?: return null
        val calendar = Calendar.getInstance().apply {
            add(Calendar.MINUTE, minutes)
        }

        return AlarmModel(
            id = UUID.randomUUID().toString(),
            hour = calendar.get(Calendar.HOUR_OF_DAY),
            minute = calendar.get(Calendar.MINUTE),
            isOn = true,
            message = "Quick Alarm",
            datesOfWeek = null,
            date = calendar.timeInMillis
        )
    }
}
