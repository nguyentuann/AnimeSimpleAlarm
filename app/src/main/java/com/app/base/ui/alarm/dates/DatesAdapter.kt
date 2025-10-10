package com.app.base.ui.alarm.dates

import android.view.View
import com.app.base.databinding.DateItemBinding
import com.brally.mobile.base.adapter.BaseListAdapter

class DatesAdapter(
    private val selectDates: (List<Int>) -> Unit
) : BaseListAdapter<Int, DateItemBinding>() {

    private val selectedPositions = mutableSetOf<Int>()

    override fun bindData(binding: DateItemBinding, item: Int, position: Int) {
        with(binding) {
            tvDateName.text = root.context.getString(item)

            val isSelected = selectedPositions.contains(position)

            // Hiển thị icon tick nếu item đang được chọn
            btnCheck.visibility = if (isSelected) View.VISIBLE else View.GONE

            // Khi người dùng click
            root.setOnClickListener {
                if (isSelected) {
                    selectedPositions.remove(position)
                } else {
                    selectedPositions.add(position)
                }

                // Cập nhật lại item hiển thị
                notifyItemChanged(position)

                // Trả danh sách vị trí được chọn ra ngoài
                selectDates(selectedPositions.toList())
            }
        }
    }
}