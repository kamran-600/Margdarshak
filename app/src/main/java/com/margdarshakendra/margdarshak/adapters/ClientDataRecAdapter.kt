package com.margdarshakendra.margdarshak.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.margdarshakendra.margdarshak.R
import com.margdarshakendra.margdarshak.databinding.SingleRowClientDataBinding
import com.margdarshakendra.margdarshak.models.ClientDataResponse
import com.margdarshakendra.margdarshak.utils.Constants.TAG
import java.net.URL
import java.util.regex.Matcher
import java.util.regex.Pattern


class ClientDataRecAdapter(private val context: Context,
                           private val showPopMenu: (View, Int, String, String) -> Unit) :
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
        val singleRowClientDataBinding =
            SingleRowClientDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(singleRowClientDataBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    /*    holder.binding.pincode.visibility = View.VISIBLE
        holder.binding.div1.visibility = View.VISIBLE
        holder.binding.div2.visibility = View.VISIBLE
        holder.binding.div3.visibility = View.VISIBLE
        holder.binding.div4.visibility = View.VISIBLE
        holder.binding.div5.visibility = View.VISIBLE
        holder.binding.div6.visibility = View.VISIBLE


        holder.binding.state.text = clientData.state
        holder.binding.district.text = clientData.district
        holder.binding.pincode.text = clientData.pincode*/

        val clientData = getItem(position)

        val address = listOf(clientData.district, clientData.state, clientData.pincode)

        holder.binding.address.apply {
            text = address.filterNotNull().joinToString(", ")
            isSelected = true
        }
        holder.binding.userClassORQualification.text = null
        if (clientData.`class` != null) {
            holder.binding.userClassORQualification.text = clientData.`class`
            holder.binding.userClassORQualification.isSelected = true
        }
        if (clientData.qualification != null) {
            holder.binding.userClassORQualification.text = clientData.qualification
            holder.binding.userClassORQualification.isSelected = true
        }
        holder.binding.instituteORSpec.text = null
        holder.binding.linkedInCard.visibility = View.GONE
        holder.binding.instituteORSpec.visibility = View.GONE
        if (clientData.specialise != null) {
             if(isLinkedInUrl(clientData.specialise)) {
                 Log.d(TAG, "${clientData.specialise}linkedIn url true")
                 try {
                     val pathSegments = URL(clientData.specialise).path.split("/")
                     val username = pathSegments[pathSegments.indexOf("in") + 1]
                     Log.d(TAG, username)
                     holder.binding.linkedInCard.visibility = View.VISIBLE
                     holder.binding.instituteORSpec.visibility = View.GONE
                     holder.binding.linkedInLink.text = "/$username"

                     holder.binding.linkedInCard.setOnClickListener {
                         val intent = Intent(Intent.ACTION_VIEW, Uri.parse(clientData.specialise))
                         context.startActivity(intent)
                     }
                 }catch (e : Exception){
                     Log.d(TAG, e.message.toString())
                     //Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                 }

             }
             else {
                 Log.d(TAG, "${clientData.specialise} linkedIn url false")
                 holder.binding.linkedInCard.visibility = View.GONE
                 holder.binding.instituteORSpec.visibility = View.VISIBLE
                 holder.binding.instituteORSpec.text = clientData.specialise
             }
        }
        if (clientData.inst != null) {
            holder.binding.linkedInCard.visibility = View.GONE
            holder.binding.instituteORSpec.visibility = View.VISIBLE
            holder.binding.instituteORSpec.text = clientData.inst
        }

        holder.binding.name.apply {
            text = clientData.name
            isSelected = true
        }
        holder.binding.mobile.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        holder.binding.email.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        if (clientData.mobile_verified == "true") {
            holder.binding.mobile.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.check_circle,
                0,
                0,
                0
            )
        }else{
            holder.binding.mobile.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.cancel_icon,
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
        }else{
            holder.binding.email.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.cancel_icon,
                0,
                0,
                0
            )
        }

        if (clientData.usertype == "S") {
            "Student/JobSeeker".also { holder.binding.userType.text = it }
        } else "Counsellor/Trainer".also { holder.binding.userType.text = it }

        Glide.with(context).load(clientData.pic).placeholder(R.drawable.account_circle)
            .into(holder.binding.profileImage)


        holder.binding.morePopMenu.setOnClickListener {
            showPopMenu(it, clientData.userID, clientData.name, clientData.usertype)
        }

    }

    private fun isLinkedInUrl(url: String): Boolean {

        //val linkedInPattern = Regex("https?://(www\\.)?linkedin\\.com/in/[a-zA-Z0-9_-]+/?")
        val regex = "^(http(s?)://)?([\\w\\d\\-]*\\.)?linkedin.com/[^.\\s]*\$"
        val linkedInPattern: Pattern = Pattern.compile(regex)
        return linkedInPattern.matcher(url).matches()
    }

    class ViewHolder(val binding: SingleRowClientDataBinding) :
        RecyclerView.ViewHolder(binding.root)

}