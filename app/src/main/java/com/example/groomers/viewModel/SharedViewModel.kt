package com.example.groomers.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class SharedViewModel : ViewModel() {
    private val _selectedItem = MutableLiveData<Event<String>>()
    val selectedItem: LiveData<Event<String>> get() = _selectedItem

    fun selectItem(item: String) {
        _selectedItem.value = Event(item)
    }
}
