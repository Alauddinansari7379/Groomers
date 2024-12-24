package com.example.pethouse.Vender
import HomeAdapter
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pethouse.R
import com.example.pethouse.views.activities.Item


class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerView2: RecyclerView
    private lateinit var recyclerView3: RecyclerView
    private lateinit var recyclerView4: RecyclerView
    private lateinit var recyclerView5: RecyclerView
    private lateinit var recyclerView6: RecyclerView


    private lateinit var adapter: HomeAdapter
    private val itemList = listOf(

        Item(R.drawable.shake, "shake"),
        Item(R.drawable.desert, "desert "),
        Item(R.drawable.donut, "donut "),
        Item(R.drawable.fruits, "fruits "),
        Item(R.drawable.burger, "burger")
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        adapter = HomeAdapter(itemList)
        recyclerView.adapter = adapter


        recyclerView2 = view.findViewById(R.id.recyclerView2)
        recyclerView2.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        adapter = HomeAdapter(itemList)
        recyclerView2.adapter = adapter


        recyclerView3 = view.findViewById(R.id.recyclerView3)
        recyclerView3.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        adapter = HomeAdapter(itemList)
        recyclerView3.adapter = adapter

        
        recyclerView4 = view.findViewById(R.id.recyclerView4)
        recyclerView4.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        adapter = HomeAdapter(itemList)
        recyclerView4.adapter = adapter


        recyclerView5 = view.findViewById(R.id.recyclerView5)
        recyclerView5.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        adapter = HomeAdapter(itemList)
        recyclerView5.adapter = adapter


        recyclerView6 = view.findViewById(R.id.recyclerView6)
        recyclerView6.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        adapter = HomeAdapter(itemList)
        recyclerView6.adapter = adapter


    }
}

