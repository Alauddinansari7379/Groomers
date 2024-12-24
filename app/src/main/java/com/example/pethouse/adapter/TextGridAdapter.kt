package com.example.pethouse.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pethouse.R

class TextGridAdapter(private val textList: List<String>) :
    RecyclerView.Adapter<TextGridAdapter.TextViewHolder>() {

    // ViewHolder for the single row
    class TextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemText: TextView = itemView.findViewById(R.id.itemText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_grid, parent, false) // Use single-row layout
        return TextViewHolder(view)
    }

    override fun onBindViewHolder(holder: TextViewHolder, position: Int) {
        holder.itemText.text = textList[position] // Bind text to TextView
    }

    override fun getItemCount(): Int = textList.size
}
