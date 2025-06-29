package com.example.groomers.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.groomers.R
import com.example.groomers.databinding.ItemProfileBinding
import com.example.groomers.model.modelmultiuserlist.Result

class ProfileAdapter(
    private val profiles: List<Result>,
    private val onItemClick: (Result) -> Unit
) : RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>() {

    inner class ProfileViewHolder(private val binding: ItemProfileBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(profile: Result) {
            binding.profileName.text = profile.username
            // Set profile image if available, currently using placeholder
            binding.profileImage.setImageResource(R.drawable.profile)
            val imageUrl = "https://groomers.co.in/public/uploads/${profile.image}"

            Glide.with(binding.profileImage.context)
                .load(imageUrl)
                .placeholder(R.drawable.profile) // optional placeholder
                // optional error image
                .into(binding.profileImage)

            binding.root.setOnClickListener {
                onItemClick(profile)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val binding = ItemProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProfileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        holder.bind(profiles[position])
    }

    override fun getItemCount(): Int = profiles.size
}
