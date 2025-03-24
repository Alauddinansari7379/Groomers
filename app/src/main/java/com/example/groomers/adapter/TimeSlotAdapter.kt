package com.example.groomers.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.groomers.R
import com.example.groomers.databinding.ItemTimeSlotBinding
class TimeSlotAdapter(
    private val timeSlots: List<Pair<String, String>>,
    private val onTimeSelected: (String, String, Int) -> Unit
) : RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder>() {

    private var selectedPosition = -1
    private var seatCounts = MutableList(timeSlots.size) { 1 }

    inner class TimeSlotViewHolder(val binding: ItemTimeSlotBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSlotViewHolder {
        val binding = ItemTimeSlotBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimeSlotViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimeSlotViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val (startTime, endTime) = timeSlots[position]

        holder.binding.tvStartTime.text = startTime
        holder.binding.tvEndTime.text = endTime
        holder.binding.tvSeatCount.text = seatCounts[position].toString()

        // Highlight selected slot
        if (selectedPosition == position) {
            holder.binding.cardTimeSlot.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.selected_bg))
            holder.binding.tvStartTime.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.selected_text))
            holder.binding.tvEndTime.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.selected_text))
        } else {
            holder.binding.cardTimeSlot.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.unselected_bg))
            holder.binding.tvStartTime.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.unselected_text))
            holder.binding.tvEndTime.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.unselected_text_secondary))
        }

        holder.binding.cardTimeSlot.setOnClickListener {
            selectedPosition = position
            onTimeSelected(startTime, endTime, seatCounts[position])
            notifyDataSetChanged()
        }

        holder.binding.btnIncreaseSeat.setOnClickListener {
            seatCounts[position]++
            holder.binding.tvSeatCount.text = seatCounts[position].toString()
            onTimeSelected(startTime, endTime, seatCounts[position])
        }

        holder.binding.btnDecreaseSeat.setOnClickListener {
            if (seatCounts[position] > 1) {
                seatCounts[position]--
                holder.binding.tvSeatCount.text = seatCounts[position].toString()
                onTimeSelected(startTime, endTime, seatCounts[position])
            }
        }
    }

    override fun getItemCount(): Int = timeSlots.size
}
