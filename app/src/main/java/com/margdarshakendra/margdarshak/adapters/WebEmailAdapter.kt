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
import com.margdarshakendra.margdarshak.databinding.SingleRowWebEmailBinding
import com.margdarshakendra.margdarshak.models.WebEmailsResponse
import java.text.SimpleDateFormat
import java.util.Locale

class WebEmailAdapter(
    private val context: Context,
    private val sendRowData: (String?, View, String?, View, String?, View, String?, String?, View, String?, View, String?, View) -> Unit
) :
    ListAdapter<WebEmailsResponse.Message, WebEmailAdapter.ViewHolder>(DiffUtilBack()) {

    class DiffUtilBack : DiffUtil.ItemCallback<WebEmailsResponse.Message>() {
        override fun areItemsTheSame(
            oldItem: WebEmailsResponse.Message,
            newItem: WebEmailsResponse.Message
        ): Boolean {
            return oldItem.userID == newItem.userID
        }

        override fun areContentsTheSame(
            oldItem: WebEmailsResponse.Message,
            newItem: WebEmailsResponse.Message
        ): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleRowWebEmailBinding =
            SingleRowWebEmailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(singleRowWebEmailBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        val webEmailData = getItem(position)

        holder.binding.subject.text = webEmailData.subject
        holder.binding.userName.text = webEmailData.name

        webEmailData.email_date?.let {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault())
            val date = dateFormat.parse(it)
            val dateFormatWithAMPM = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
            holder.binding.dateTime.text = date?.let { it1 -> dateFormatWithAMPM.format(it1) }
        }
        var lastViewedDate :String? = null
        webEmailData.last_viewed?.let {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault())
            val date = dateFormat.parse(it)
            val dateFormatWithAMPM = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
            lastViewedDate = date?.let { it1 -> dateFormatWithAMPM.format(it1) }

        }

        if(webEmailData.unique_key != null){

            // means it is sentbox data
            webEmailData.edate?.let {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault())
                val date = dateFormat.parse(it)
                val dateFormatWithAMPM = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
                holder.binding.dateTime.text = date?.let { it1 -> dateFormatWithAMPM.format(it1) }
            }

        }

        if(webEmailData.sentmailID != null){
            webEmailData.views?.let {
                holder.binding.views.text = "$it views"
            }
        }else holder.binding.views.visibility = View.GONE





        Glide.with(context).load(webEmailData.pic).placeholder(R.drawable.account_circle).into(holder.binding.image)

        holder.binding.root.setOnClickListener {

            sendRowData(
                webEmailData.pic,
                holder.binding.image,
                webEmailData.name,
                holder.binding.userName,
                webEmailData.subject,
                holder.binding.subject,
                webEmailData.body,
                holder.binding.dateTime.text.toString(),
                holder.binding.dateTime,
                webEmailData.views,
                holder.binding.views,
                lastViewedDate,
                holder.binding.root
            )
        }


    }

    class ViewHolder(val binding: SingleRowWebEmailBinding) :
        RecyclerView.ViewHolder(binding.root)

}