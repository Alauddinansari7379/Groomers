package com.example.groomers.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.groomers.R
import com.example.groomers.databinding.ItemTimeSlotBinding
class TimeSlotAdapter(
    private var timeSlots: List<Triple<String, String, Int>>, // Triple<StartTime, EndTime, SeatAvailable>
    private val onSeatsSelected: (String, String, Int) -> Unit
) : RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder>() {

    private val seatCount = mutableMapOf<Int, Int>().withDefault { 1 }
    private var selectedPosition: Int? = null // Track selected slot
    private var editingPosition: Int? = null // Track which slot is being edited

    inner class TimeSlotViewHolder(val binding: ItemTimeSlotBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSlotViewHolder {
        val binding = ItemTimeSlotBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimeSlotViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimeSlotViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val (startTime, endTime, availableSeats) = timeSlots[position]

        val selectedSeats = seatCount.getValue(position).coerceAtMost(availableSeats)
        val isSelected = (selectedPosition == position)
        val isEditing = (editingPosition == position)

        holder.binding.apply {
            tvStartTime.text = startTime
            tvEndTime.text = endTime
            tvSeats.text = "$availableSeats Seats Available" // ✅ Always show total available seats
            tvSeatCount.text = selectedSeats.toString() // ✅ Show selected seat count

            // ✅ Change background if slot is selected
            cardTimeSlot.setBackgroundResource(if (isSelected) R.drawable.selected_slot else R.drawable.bg_default)

            // ✅ Show/hide seat selection controls
            seatSelectionLayout.visibility = if (isEditing) View.VISIBLE else View.GONE
            ivIncreaseSeats.visibility = if (isEditing) View.VISIBLE else View.GONE
            ivDecreaseSeats.visibility = if (isEditing) View.VISIBLE else View.GONE
            ivEditSeats.visibility = if (isEditing) View.GONE else View.VISIBLE

            // ✅ Edit seats when edit icon is clicked
            ivEditSeats.setOnClickListener {
                val prevEditing = editingPosition
                editingPosition = position
                notifyItemChanged(prevEditing ?: -1)
                notifyItemChanged(position)
            }

            // ✅ Increase seat count (cannot exceed available seats)
            ivIncreaseSeats.setOnClickListener {
                val newCount = seatCount.getOrDefault(position, 1) + 1
                if (newCount <= availableSeats) {
                    seatCount[position] = newCount
                    updateSeatUI(holder, position)
                }
            }

            // ✅ Decrease seat count (cannot go below 1)
            ivDecreaseSeats.setOnClickListener {
                val newCount = seatCount.getOrDefault(position, 1) - 1
                if (newCount >= 1) {
                    seatCount[position] = newCount
                    updateSeatUI(holder, position)
                }
            }

            // ✅ Select slot when clicking on it
            cardTimeSlot.setOnClickListener {
                val prevSelected = selectedPosition
                selectedPosition = position
                notifyItemChanged(prevSelected ?: -1) // Refresh previous selection
                notifyItemChanged(position) // Refresh new selection

                // ✅ Send selected slot details to ViewOrderDetails
                onSeatsSelected(startTime, endTime, seatCount.getValue(position))
            }
        }
    }

    override fun getItemCount(): Int = timeSlots.size

    private fun updateSeatUI(holder: TimeSlotViewHolder, position: Int) {
        val newSeatCount = seatCount.getValue(position)
        holder.binding.tvSeatCount.text = newSeatCount.toString() // ✅ Show selected seat count
        onSeatsSelected(timeSlots[position].first, timeSlots[position].second, newSeatCount) // ✅ Pass updated data
    }

    fun updateData(newSlots: List<Triple<String, String, Int>>) {
        timeSlots = newSlots
        seatCount.clear()
        selectedPosition = null
        editingPosition = null
        notifyDataSetChanged()
    }
}
