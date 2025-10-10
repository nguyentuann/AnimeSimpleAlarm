package com.app.base.ui.alarm.character

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.base.R
import com.app.base.databinding.CharacterItemBinding
import com.brally.mobile.base.adapter.BaseListAdapter

class CharacterAdapter(
    private val selectCharacter: (Int) -> Unit
) : BaseListAdapter<Int, CharacterItemBinding>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    override fun bindData(binding: CharacterItemBinding, item: Int, position: Int) {
        with(binding) {
            imgCharacter.setImageResource(item)

            val isSelected = selectedPosition == position
            cardCharacter.strokeWidth = if (isSelected) 10 else 0
            val color = if (isSelected) {
                ContextCompat.getColor(context, R.color.secondary)
            } else {
                ContextCompat.getColor(context, R.color.transparent)
            }
            cardCharacter.strokeColor = color

            // Hiệu ứng scale khi chọn
            val scale = if (isSelected) 1.05f else 1f
            imgCharacter.animate()
                .scaleX(scale)
                .scaleY(scale)
                .setDuration(150)
                .start()

            // Bắt sự kiện click
            root.setOnClickListener {
                val previous = selectedPosition
                selectedPosition = position
                if (previous != RecyclerView.NO_POSITION) {
                    notifyItemChanged(previous)
                }
                notifyItemChanged(position)
                selectCharacter(item)
            }
        }
    }
}
