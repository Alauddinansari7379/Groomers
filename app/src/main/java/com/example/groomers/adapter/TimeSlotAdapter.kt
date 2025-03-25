package com.example.groomers.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.groomers.databinding.ItemTimeSlotBinding

class TimeSlotAdapter(
    private val timeSlots: List<Pair<String, String>>, // Pair<StartTime, EndTime>
    private val onSeatsSelected: (String, String, Int) -> Unit
) : RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder>() {

    private val seatCount = mutableMapOf<Int, Int>()
    private val isEditingSeats = mutableMapOf<Int, Boolean>()

    inner class TimeSlotViewHolder(val binding: ItemTimeSlotBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSlotViewHolder {
        val binding = ItemTimeSlotBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimeSlotViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimeSlotViewHolder, position: Int) {
        val (startTime, endTime) = timeSlots[position]

        // Initialize seat count and editing state
        val selectedSeats = seatCount.getOrPut(position) { 1 }
        val isEditing = isEditingSeats.getOrPut(position) { false }

        holder.binding.tvStartTime.text = startTime
        holder.binding.tvEndTime.text = endTime
        holder.binding.tvSeats.text = "$selectedSeats Seat${if (selectedSeats > 1) "s" else ""}"
        holder.binding.tvSeatCount.text = selectedSeats.toString() // Ensure seat count updates correctly

        // Toggle seat selection UI based on editing mode
        if (isEditing) {
            holder.binding.seatSelectionLayout.visibility = View.VISIBLE
            holder.binding.ivIncreaseSeats.visibility = View.VISIBLE
            holder.binding.ivDecreaseSeats.visibility = View.VISIBLE
            holder.binding.ivEditSeats.visibility = View.GONE
        } else {
            holder.binding.seatSelectionLayout.visibility = View.GONE
            holder.binding.ivIncreaseSeats.visibility = View.GONE
            holder.binding.ivDecreaseSeats.visibility = View.GONE
            holder.binding.ivEditSeats.visibility = View.VISIBLE
        }

        // Edit button click listener
        holder.binding.ivEditSeats.setOnClickListener {
            isEditingSeats.keys.forEach { key -> isEditingSeats[key] = false } // Reset all other selections
            isEditingSeats[position] = true
            notifyDataSetChanged()
        }

        // Increase seats (No max limit specified, but can be added)
        holder.binding.ivIncreaseSeats.setOnClickListener {
            seatCount[position] = seatCount[position]!! + 1
            updateSeatUI(holder, position)
        }

        // Decrease seats (Min limit is 1)
        holder.binding.ivDecreaseSeats.setOnClickListener {
            if (seatCount[position]!! > 1) {
                seatCount[position] = seatCount[position]!! - 1
                updateSeatUI(holder, position)
            }
        }

        // Clicking outside closes the seat selection UI
        holder.binding.cardTimeSlot.setOnClickListener {
            if (isEditingSeats[position] == true) {
                isEditingSeats[position] = false
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int = timeSlots.size

    // Helper function to update seat UI
    private fun updateSeatUI(holder: TimeSlotViewHolder, position: Int) {
        val newSeatCount = seatCount[position]!!
        holder.binding.tvSeatCount.text = newSeatCount.toString()
        holder.binding.tvSeats.text = "$newSeatCount Seat${if (newSeatCount > 1) "s" else ""}"
        onSeatsSelected(timeSlots[position].first, timeSlots[position].second, newSeatCount)
    }
}
