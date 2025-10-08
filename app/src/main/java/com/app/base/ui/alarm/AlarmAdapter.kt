package com.app.base.ui.alarm
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.base.data.model.AlarmModel
import com.app.base.utils.TimeConverter
import com.app.base.R
import com.app.base.helpers.AlarmHelper
import com.app.base.helpers.IconHelper

class AlarmAdapter(
    private val deleteAlarm: (AlarmModel) -> Unit,
    private val editAlarm: (String) -> Unit,
    private val enableAlarm: (AlarmModel) -> Unit

) : ListAdapter<AlarmModel, AlarmAdapter.AlarmViewHolder>(DiffCallback) {

    inner class AlarmViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: CardView = view.findViewById(R.id.card_item)
        val nextAlarm: TextView = view.findViewById(R.id.next_alarm)
        val imgDayNight: ImageView = view.findViewById(R.id.day_or_night)
        val tvTime: TextView = view.findViewById(R.id.tv_time)
        val tvDates: TextView = view.findViewById(R.id.tv_dates)
        val switchOnOff: SwitchCompat = view.findViewById(R.id.on_off)
        val btnEdit: ImageButton = view.findViewById(R.id.btn_edit)
        val btnDelete: ImageButton = view.findViewById(R.id.btn_delete)

        val context: Context = itemView.context

        fun bind(item: AlarmModel) {
            // set dữ liệu
            tvTime.text = TimeConverter.convertTimeToString(item.hour, item.minute)
            tvDates.text = TimeConverter.convertListDateToString(context = context, days = item.dateOfWeek)

            nextAlarm.text = nextDateOfAlarm(context = context, item = item)

            imgDayNight.setImageResource(
                IconHelper.getIconResourceForAlarm(item.hour)
            )

            switchOnOff.setOnCheckedChangeListener(null)
            switchOnOff.isChecked = item.isOn
            listOf(card, imgDayNight, tvTime, tvDates, btnEdit, btnDelete).forEach {
                it.isSelected = item.isOn
            }
            switchOnOff.setOnCheckedChangeListener { _, isChecked ->
                item.isOn = isChecked
                listOf(card, imgDayNight, tvTime, tvDates, btnEdit, btnDelete).forEach {
                    it.isSelected = item.isOn
                }
                enableAlarm(item)
            }

            // gán sự kiện
            btnDelete.setOnClickListener { deleteAlarm(item) }
            btnEdit.setOnClickListener { editAlarm(item.id) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.alarm_item, parent, false)
        return AlarmViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<AlarmModel>() {
        override fun areItemsTheSame(oldItem: AlarmModel, newItem: AlarmModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AlarmModel, newItem: AlarmModel): Boolean {
            return oldItem == newItem
        }
    }

    fun nextDateOfAlarm(context: Context, item: AlarmModel): String {
        return if (!item.dateOfWeek.isNullOrEmpty()) {
            TimeConverter.nameDateOfWeek(
                context = context,
                calendar = AlarmHelper.getNextDayOfWeek(
                    item.hour,
                    item.minute,
                    item.dateOfWeek!!
                )
            )
        } else if (item.date != null) {
            TimeConverter.dayMonthYearFormatFromCalendar(item.date!!)
        } else {
            ""
        }
    }
}