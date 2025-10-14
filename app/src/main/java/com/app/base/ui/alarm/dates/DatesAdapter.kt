package com.app.base.ui.alarm.dates

import android.view.View
import com.app.base.databinding.DateItemBinding
import com.app.base.utils.LogUtil
import com.brally.mobile.base.adapter.BaseListAdapter

class DatesAdapter(
    var positions: List<Int>,
    private val selectDateOfWeek: (List<Int>) -> Unit,
) : BaseListAdapter<Int, DateItemBinding>() {

    private val selectedPositions = positions.toMutableList()

    override fun bindData(binding: DateItemBinding, item: Int, position: Int) {

        LogUtil.log(positions.toString())

        LogUtil.log(selectedPositions.toString())

        with(binding) {
            tvDateName.text = root.context.getString(item)

            val isSelected = selectedPositions.contains(position+1)

            // Hiển thị icon tick nếu item đang được chọn
            btnCheck.visibility = if (isSelected) View.VISIBLE else View.GONE

            // Khi người dùng click
            root.setOnClickListener {
                val value = position + 1
                if (selectedPositions.contains(value)) {
                    selectedPositions.remove(value)
                } else {
                    selectedPositions.add(value)
                }

                notifyItemChanged(position)
                selectDateOfWeek(selectedPositions.toList())
            }

        }
    }
}