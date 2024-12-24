package com.example.pethouse.Vender

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pethouse.R
import com.example.pethouse.adapter.OrderAdapter

class list : Fragment(R.layout.fragment_list) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var orderList: List<Order>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewOrders)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Sample data for orders
        orderList = listOf(
            Order("001", "John Doe", "Pending"),
            Order("002", "Jane Smith", "Shipped"),
            Order("003", "Mike Johnson", "Delivered"),
            Order("004", "Alice Brown", "Pending"),
            Order("005", "David White", "Shipped")
        )

        // Set up the adapter
        orderAdapter = OrderAdapter(orderList)
        recyclerView.adapter = orderAdapter
    }
}
