package com.app.base.ui.timer_stopwatch

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.base.databinding.LapItemBinding

class LapListAdapter(private val laps: List<String>) :
    RecyclerView.Adapter<LapListAdapter.LapViewHolder>() {

    inner class LapViewHolder(val binding: LapItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LapViewHolder {
        val binding = LapItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LapViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: LapViewHolder, position: Int) {
        holder.binding.lapItem.text = laps[position]
        holder.binding.lapIndex.text = (position + 1).toString()
    }

    override fun getItemCount() = laps.size
}