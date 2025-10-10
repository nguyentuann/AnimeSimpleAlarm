package com.app.base.ui.alarm.sound

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import com.app.base.R
import com.app.base.databinding.SoundItemBinding
import com.app.base.utils.AppConstants
import com.brally.mobile.base.adapter.BaseListAdapter

class SoundAdapter(
    private val selectSound: (Int) -> Unit
) : BaseListAdapter<Int, SoundItemBinding>() {

    private var selectedPosition = RecyclerView.NO_POSITION
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false

    override fun bindData(binding: SoundItemBinding, item: Int, position: Int) {
        with(binding) {
            val context = root.context

            val soundName = binding.root.context.getString(AppConstants.getNameSoundById(item))
            tvSoundName.text = soundName

            val baseColor = ContextCompat.getColor(context, R.color.secondary)
            val backgroundColor = if (selectedPosition == position)
                ColorUtils.setAlphaComponent(baseColor, 100)
            else
                ContextCompat.getColor(context, R.color.transparent)

            cardSound.setCardBackgroundColor(backgroundColor)

            // Cập nhật icon play/pause
            val iconRes = if (selectedPosition == position && isPlaying)
                R.drawable.ic_pause
            else
                R.drawable.ic_play
            btnPlayPause.setImageResource(iconRes)

            // Sự kiện chọn item (không phát)
            root.setOnClickListener {
                handlePlayPause(context, item, position)
                val previousPos = selectedPosition
                selectedPosition = position
                notifyItemChanged(previousPos)
                notifyItemChanged(selectedPosition)
                selectSound(item)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun handlePlayPause(context: Context, soundRes: Int, position: Int) {
        val previousPos = selectedPosition
        val isSameSound = position == selectedPosition

        if (isSameSound && isPlaying) {
            // ⏸ Tạm dừng
            mediaPlayer?.pause()
            isPlaying = false
        } else {
            // 🛑 Dừng bài cũ
            mediaPlayer?.stop()
            mediaPlayer?.release()

            // ▶️ Phát bài mới
            mediaPlayer = MediaPlayer.create(context, soundRes)
            mediaPlayer?.start()
            isPlaying = true
            selectedPosition = position
        }

        // 🔁 Cập nhật lại item cũ & mới
        notifyItemChanged(previousPos)
        notifyItemChanged(selectedPosition)

        // 🎵 Khi phát xong thì reset trạng thái
        mediaPlayer?.setOnCompletionListener {
            isPlaying = false
            notifyItemChanged(selectedPosition)
        }

        // Gọi callback nếu cần
        selectSound(soundRes)
    }



    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
