package com.example.groomers.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.groomers.databinding.CategoryCell1Binding
import com.example.groomers.model.modelcategory.Result

class CategoryAdapter(
    private val categoryList: List<Result>,
    private val onItemClick: (Result) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(val binding: CategoryCell1Binding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding =
            CategoryCell1Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun getItemCount(): Int = categoryList.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val item = categoryList[position]
        holder.binding.tvCategory.text = item.category_name
        Glide.with(holder.binding.root.context)
            .load("https://groomers.co.in/public/uploads/" + item.category_image)
            .into(holder.binding.ivCategory)

        holder.binding.root.setOnClickListener {
            onItemClick(item)
        }
    }
}
