package com.example.groomers.adapter


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.groomers.activity.Login
import com.example.groomers.databinding.ItemAddressBinding
import com.example.groomers.model.modeladdresslist.Result
import com.example.groomers.sharedpreferences.SessionManager

class AddressListAdapter(
    private var addressList: List<Result>,
    private val clickEvent: ClickEvent, val context: Context// Pass clickEvent correctly
) : RecyclerView.Adapter<AddressListAdapter.AddressViewHolder>() {

    inner class AddressViewHolder(private val binding: ItemAddressBinding) :
        RecyclerView.ViewHolder(binding.root) {
        lateinit var sessionManager: SessionManager

        fun bind(address: Result, position: Int) {
            binding.tvAddress.text = address.address
            binding.tvZipCode.text = "Zip Code: ${address.zip_code}"
            binding.tvCity.text = "City: ${address.city}"
            binding.tvState.text = "State: ${address.name}"
            binding.tvCountry.text = "Country: ${address.countryname}"
            sessionManager = SessionManager(context)

            if (sessionManager.address.isNullOrEmpty()){
                sessionManager.addressId = "0"
                sessionManager.address =addressList[0].address + " " + addressList[0].city + " " + addressList[0].name + " " + addressList[0].countryname + " " + addressList[0].zip_code
            }

            if (addressList.size == 1) {
                sessionManager.addressId = position.toString()
                sessionManager.address =
                    address.address + " " + address.city + " " + address.name + " " + address.countryname + " " + address.zip_code
            }
            if (sessionManager.addressId?.toInt() == position) {
                binding.tvAddressStatues.visibility = View.VISIBLE
            } else {
                binding.tvAddressStatues.visibility = View.GONE
            }


            // Handle Edit Click
            binding.ivEdit.setOnClickListener {
                clickEvent.edit(address)
            }
            binding.root.setOnClickListener {
                SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Are you sure want to make primary address?")
                    .setCancelText("No")
                    .setConfirmText("Yes")
                    .showCancelButton(true)
                    .setConfirmClickListener { sDialog ->
                        sDialog.cancel()
                        sessionManager.addressId = position.toString()
                        sessionManager.address =
                            address.address + " " + address.city + " " + address.name + " " + address.countryname + " " + address.zip_code
                        notifyDataSetChanged()
                    }
                    .setCancelClickListener { sDialog ->
                        sDialog.cancel()
                    }
                    .show()
            }

            // Handle Delete Click
            binding.ivDelete.setOnClickListener {
                clickEvent.delete(address.id) // Pass address ID to delete
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding =
            ItemAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.bind(addressList[position], position)
    }

    override fun getItemCount(): Int = addressList.size

    fun updateList(newList: List<Result>) {
        addressList = newList
        notifyDataSetChanged()
    }
}

// Updated Interface to include address ID in delete method
interface ClickEvent {
    fun edit(address: Result)
    fun delete(addressId: Int) // Pass address ID for delete
}
