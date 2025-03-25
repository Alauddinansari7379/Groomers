package com.example.groomers.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.groomers.databinding.ItemTimeSlotBinding
class TimeSlotAdapter(
    private var timeSlots: List<Pair<String, String>>, // Pair<StartTime, EndTime>
    private val onSeatsSelected: (String, String, Int) -> Unit
) : RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder>() {

    private val seatCount = mutableMapOf<Int, Int>().withDefault { 1 }
    private var editingPosition: Int? = null // Track the currently edited item

    inner class TimeSlotViewHolder(val binding: ItemTimeSlotBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSlotViewHolder {
        val binding = ItemTimeSlotBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimeSlotViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimeSlotViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val (startTime, endTime) = timeSlots[position]

        val selectedSeats = seatCount.getValue(position) // Safe default to 1
        val isEditing = (editingPosition == position) // Only one row can be edited at a time

        holder.binding.apply {
            tvStartTime.text = startTime
            tvEndTime.text = endTime
            tvSeats.text = "$selectedSeats Seat${if (selectedSeats > 1) "s" else ""}"
            tvSeatCount.text = selectedSeats.toString()

            seatSelectionLayout.visibility = if (isEditing) View.VISIBLE else View.GONE
            ivIncreaseSeats.visibility = if (isEditing) View.VISIBLE else View.GONE
            ivDecreaseSeats.visibility = if (isEditing) View.VISIBLE else View.GONE
            ivEditSeats.visibility = if (isEditing) View.GONE else View.VISIBLE
        }

        // Edit button click listener
        holder.binding.ivEditSeats.setOnClickListener {
            val prevEditingPos = editingPosition
            editingPosition = position
            notifyItemChanged(prevEditingPos ?: -1) // Reset previous edited row
            notifyItemChanged(position) // Update newly selected row
        }

        // Increase seats
        holder.binding.ivIncreaseSeats.setOnClickListener {
            seatCount[position] = (seatCount[position] ?: 1) + 1
            updateSeatUI(holder, position)
        }

        // Decrease seats (Min 1)
        holder.binding.ivDecreaseSeats.setOnClickListener {
            val newCount = (seatCount[position] ?: 1) - 1
            if (newCount >= 1) {
                seatCount[position] = newCount
                updateSeatUI(holder, position)
            }
        }

        // Clicking outside the selection UI closes it
        holder.binding.cardTimeSlot.setOnClickListener {
            if (editingPosition == position) {
                editingPosition = null
                notifyItemChanged(position)
            }
        }
    }

    override fun getItemCount(): Int = timeSlots.size

    // Helper function to update seat UI
    private fun updateSeatUI(holder: TimeSlotViewHolder, position: Int) {
        val newSeatCount = seatCount.getValue(position)
        holder.binding.tvSeatCount.text = newSeatCount.toString()
        holder.binding.tvSeats.text = "$newSeatCount Seat${if (newSeatCount > 1) "s" else ""}"
        onSeatsSelected(timeSlots[position].first, timeSlots[position].second, newSeatCount)
    }

    // Function to update adapter data dynamically
    fun updateData(newSlots: List<Pair<String, String>>) {
        timeSlots = newSlots
        seatCount.clear() // Reset seat selections
        editingPosition = null
        notifyDataSetChanged()
    }
}
