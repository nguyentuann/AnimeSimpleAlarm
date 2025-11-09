package com.app.base.ui.alarm

import android.content.Context
import com.app.base.data.model.AlarmModel
import com.app.base.databinding.AlarmItemBinding
import com.app.base.helpers.AlarmHelper
import com.app.base.helpers.IconHelper
import com.app.base.utils.TimeConverter
import com.brally.mobile.base.adapter.BaseListAdapter

class AlarmAdapter(
    private val deleteAlarm: (AlarmModel) -> Unit,
    private val editAlarm: (String) -> Unit,
    private val enableAlarm: (AlarmModel) -> Unit
) : BaseListAdapter<AlarmModel, AlarmItemBinding>() {

    override fun bindData(binding: AlarmItemBinding, item: AlarmModel, position: Int) {
        val context = binding.root.context

        with(binding) {
            if (!item.message.isNullOrEmpty()) {
                tvMessage.text = item.message
            }

            tvTime.text = TimeConverter.convertTimeToString(item.hour, item.minute)
            tvDates.text = TimeConverter.convertListDateToString(context, item.datesOfWeek)
            nextAlarm.text = nextDateOfAlarm(context, item)

            dayOrNight.setImageResource(IconHelper.getIconResourceForAlarm(item.hour))

            onOff.setOnCheckedChangeListener(null)
            onOff.isChecked = item.isOn

            listOf(cardItem, dayOrNight, tvTime, tvDates, btnEdit, btnDelete).forEach {
                it.isSelected = item.isOn
            }

            onOff.setOnCheckedChangeListener { _, isChecked ->
                item.isOn = isChecked
                listOf(cardItem, dayOrNight, tvTime, tvDates, btnEdit, btnDelete).forEach {
                    it.isSelected = item.isOn
                }
                enableAlarm(item)
            }

            btnDelete.setOnClickListener { deleteAlarm(item) }
            btnEdit.setOnClickListener { editAlarm(item.id) }
        }
    }

    private fun nextDateOfAlarm(context: Context, item: AlarmModel): String {
        return if (!item.datesOfWeek.isNullOrEmpty()) {
            TimeConverter.nameDateOfWeek(
                context,
                AlarmHelper.getNextDayOfWeek(item.hour, item.minute, item.datesOfWeek!!)
            )
        } else if (item.date != null) {
            TimeConverter.dayMonthYearFormatFromCalendar(item.date!!)
        } else ""
    }
}
