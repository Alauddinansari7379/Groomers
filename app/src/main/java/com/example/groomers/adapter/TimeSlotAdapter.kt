package com.example.groomers.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.ehcf.Helper.changeDateFormat6
import com.example.ehcf.Helper.convertTo12Hour
import com.example.groomers.R
import com.example.groomers.databinding.ItemTimeSlotBinding
import com.example.groomers.model.modelslotbooking.Result
class TimeSlotAdapter(
    private var timeSlots: List<Result>, // ✅ Use Result model instead of Triple
    private val onSeatsSelected: (Result, Int) -> Unit // ✅ Pass the full Result object
) : RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder>() {

    private val seatCount = mutableMapOf<Int, Int>().withDefault { 1 }
    private var selectedPosition: Int? = null
    private var editingPosition: Int? = null

    inner class TimeSlotViewHolder(val binding: ItemTimeSlotBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSlotViewHolder {
        val binding = ItemTimeSlotBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimeSlotViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TimeSlotViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val result = timeSlots[position] // ✅ Get full Result object
        val selectedSeats = seatCount.getValue(position).coerceAtMost(result.seat_available)
        val isSelected = (selectedPosition == position)
        val isEditing = (editingPosition == position)

        holder.binding.apply {
            tvStartTime.text = convertTo12Hour(result.start_time)
            tvEndTime.text = convertTo12Hour(result.end_time)
            tvSeats.text = "${result.seat_available} Seats Available"
            tvSeatCount.text = selectedSeats.toString()

            cardTimeSlot.setBackgroundResource(if (isSelected) R.drawable.selected_slot else R.drawable.bg_default)

            seatSelectionLayout.visibility = if (isEditing) View.VISIBLE else View.GONE
            ivIncreaseSeats.visibility = if (isEditing) View.VISIBLE else View.GONE
            ivDecreaseSeats.visibility = if (isEditing) View.VISIBLE else View.GONE
            ivEditSeats.visibility = if (isEditing) View.GONE else View.VISIBLE

            ivEditSeats.setOnClickListener {
                val prevEditing = editingPosition
                editingPosition = position
                notifyItemChanged(prevEditing ?: -1)
                notifyItemChanged(position)
            }

            ivIncreaseSeats.setOnClickListener {
                val newCount = seatCount.getOrDefault(position, 1) + 1
                if (newCount <= result.seat_available) {
                    seatCount[position] = newCount
                    updateSeatUI(holder, position)
                }
            }

            ivDecreaseSeats.setOnClickListener {
                val newCount = seatCount.getOrDefault(position, 1) - 1
                if (newCount >= 1) {
                    seatCount[position] = newCount
                    updateSeatUI(holder, position)
                }
            }

            cardTimeSlot.setOnClickListener {
                val prevSelected = selectedPosition
                selectedPosition = position
                notifyItemChanged(prevSelected ?: -1)
                notifyItemChanged(position)

                onSeatsSelected(result, seatCount.getValue(position)) // ✅ Pass full Result object
            }
        }
    }

    override fun getItemCount(): Int = timeSlots.size

    private fun updateSeatUI(holder: TimeSlotViewHolder, position: Int) {
        val newSeatCount = seatCount.getValue(position)
        seat=newSeatCount.toString()
        holder.binding.tvSeatCount.text = newSeatCount.toString()
        onSeatsSelected(timeSlots[position], newSeatCount) // ✅ Pass full Result object
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newSlots: List<Result>) {
        timeSlots = newSlots
        seatCount.clear()
        selectedPosition = null
        editingPosition = null
        notifyDataSetChanged()
    }
    companion object{
        var seat=""
    }
}
