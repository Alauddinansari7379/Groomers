package com.example.groomers.adapter



import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.groomers.databinding.ItemAddressBinding
import com.example.groomers.model.modeladdresslist.Result

class AddressListAdapter(
    private var addressList: List<Result>,
    private val clickEvent: ClickEvent // Pass clickEvent correctly
) : RecyclerView.Adapter<AddressListAdapter.AddressViewHolder>() {

    inner class AddressViewHolder(private val binding: ItemAddressBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(address: Result) {
            binding.tvAddress.text = address.address
            binding.tvZipCode.text = "Zip Code: ${address.zip_code}"
            binding.tvCity.text = "City: ${address.city}"
            binding.tvState.text = "State: ${address.name}"
            binding.tvCountry.text = "Country: ${address.countryname}"

            // Handle Edit Click
            binding.ivEdit.setOnClickListener {
                clickEvent.edit(address)
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
        holder.bind(addressList[position])
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
