package com.example.groomers.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.groomers.R
import com.example.groomers.databinding.VendorsRowBinding
import com.example.groomers.model.modelvendorlists.Result

class VendorsListAdapter(private var serviceList: List<Result>,
                            private val onItemClick: (Result) -> Unit
    ) : RecyclerView.Adapter<VendorsListAdapter.VendorsViewHolder>() {

        inner class VendorsViewHolder(val binding: VendorsRowBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(service: Result) {
                with(binding) {
                    val imageUrl = "https://groomers.co.in/public/uploads/${service.profile_picture}"

                    Glide.with(root.context)
                        .load(imageUrl)
                        .placeholder(R.drawable.noimage)
                        .into(vendorImage)

                    root.setOnClickListener {
                        onItemClick(service) // Call click listener
                    }

                    vendorName.text = service.name
//                    vendorRating.text = service.rating
//                    vendorReviews.text = "(${service.reviews})"
//                    vendorDistance.text = service.distance

                    vendorRating.text = "4.8"
                    vendorReviews.text = "(345)"
                    vendorDistance.text = "10 KM"
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VendorsViewHolder {
            val binding = VendorsRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return VendorsViewHolder(binding)
        }

        override fun onBindViewHolder(holder: VendorsViewHolder, position: Int) {
            holder.bind(serviceList[position])
        }

        override fun getItemCount(): Int = serviceList.size

        fun updateData(newList: List<Result>) {
            serviceList = newList
            notifyDataSetChanged()
        }
    }
