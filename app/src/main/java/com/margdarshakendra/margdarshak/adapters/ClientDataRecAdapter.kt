package com.margdarshakendra.margdarshak.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.margdarshakendra.margdarshak.R
import com.margdarshakendra.margdarshak.databinding.SingleRowClientdataBinding
import com.margdarshakendra.margdarshak.models.ClientDataResponse


class ClientDataRecAdapter(
    private val context: Context
) :
    ListAdapter<ClientDataResponse.Data, ClientDataRecAdapter.ViewHolder>(DiffUtilBack()) {

    class DiffUtilBack : DiffUtil.ItemCallback<ClientDataResponse.Data>() {
        override fun areItemsTheSame(
            oldItem: ClientDataResponse.Data,
            newItem: ClientDataResponse.Data
        ): Boolean {
            return oldItem.userID == newItem.userID
        }

        override fun areContentsTheSame(
            oldItem: ClientDataResponse.Data,
            newItem: ClientDataResponse.Data
        ): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleRowClientdataBinding =
            SingleRowClientdataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(singleRowClientdataBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.userType.visibility = View.GONE
        holder.binding.ll2.visibility = View.GONE
        holder.binding.ll3.visibility = View.GONE
        holder.binding.ll4.visibility = View.GONE
        holder.binding.ll5.visibility = View.GONE
        holder.binding.homeCompliment1.visibility = View.VISIBLE
        holder.binding.homeCompliment2.visibility = View.VISIBLE
        holder.binding.homeCompliment3.visibility = View.VISIBLE
        val clientData = getItem(position)


        clientData.state.let {
            holder.binding.state.text = it
        }
        clientData.district.let {
            holder.binding.district.text = it
        }
        clientData.pincode.let {
            holder.binding.pincode.text = it
        }
        if(clientData.`class`!= null){
            holder.binding.userClassORQualification.text = clientData.`class`
        }
        if(clientData.qualification != null){
            holder.binding.userClassORQualification.text = clientData.qualification
        }
        if(clientData.specialise != null){
            holder.binding.instituteORSpec.text = clientData.specialise
        }
       if(clientData.inst != null){
            holder.binding.instituteORSpec.text = clientData.inst
        }

        holder.binding.name.apply {
            text = clientData.name
            isSelected = true
        }

        if (clientData.mobile_verified == "true") {
            holder.binding.mobile.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.check_circle,
                0,
                0,
                0
            )
        }
        if (clientData.email_verified == "true") {
            holder.binding.email.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.check_circle,
                0,
                0,
                0
            )
        }

        if (clientData.usertype == "S") {
            "Student/JobSeeker".also { holder.binding.position.text = it }
        } else "Counsellor/Trainer".also { holder.binding.position.text = it }

        if (clientData.pic != null) {
            Glide.with(context).load(clientData.pic).into(holder.binding.profileImage)
        }


    }

    class ViewHolder(val binding: SingleRowClientdataBinding) :
        RecyclerView.ViewHolder(binding.root)

}