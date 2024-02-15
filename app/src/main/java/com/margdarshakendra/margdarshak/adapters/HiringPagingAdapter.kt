package com.margdarshakendra.margdarshak.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.margdarshakendra.margdarshak.R
import com.margdarshakendra.margdarshak.databinding.SingleRowDataBinding
import com.margdarshakendra.margdarshak.models.HiringDataResponse
import com.margdarshakendra.margdarshak.utils.Constants.TAG

class HiringPagingAdapter(
    private val context: Context,
    private val makeCall: (Int, Int, String) -> Unit,
    private val sendCloudMessage: (Int, Int, String) -> Unit,
    private val sendWhatsappMessage: (Int, Int, String) -> Unit,
    private val sendEmailMessage: (Int, Int, String) -> Unit,
    private val showPopMenu: (View, Int, Int, String) -> Unit
) : PagingDataAdapter<HiringDataResponse.ApiData.HiringData, HiringPagingAdapter.ViewHolder>(COMPARATOR) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleRowDataBinding =
            SingleRowDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(singleRowDataBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "adapter is called")
        val hiringDataList = getItem(position) ?: return

        holder.binding.position.text = hiringDataList.position
        "log(${hiringDataList.log_count})".also { holder.binding.logs.text = it }
        if (hiringDataList.usertype == "S") {
            "Student".also { holder.binding.userType.text = it }
        } else "Job Seeker".also { holder.binding.userType.text = it }

        val address = listOf(hiringDataList.district, hiringDataList.state, hiringDataList.pincode)

        holder.binding.address.apply {
            text = address.filterNotNull().joinToString(", ")
            isSelected = true
        }

        holder.binding.name.apply {
            text = hiringDataList.name
            isSelected = true
        }
        if (hiringDataList.lateDays != 0) {
            holder.binding.lateDays.text = "Late (${hiringDataList.lateDays}) Days"
        } else {
            holder.binding.lateDays.visibility = View.GONE
        }
        holder.binding.dataAdvisorName.text = hiringDataList.advisor_name

        holder.binding.mobile.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        holder.binding.email.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        if (hiringDataList.mobile_verified == "true") {
            holder.binding.mobile.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.check_circle,
                0,
                0,
                0
            )
        } else {
            holder.binding.mobile.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.cancel_icon,
                0,
                0,
                0
            )
        }
        if (hiringDataList.email_verified == "true") {
            holder.binding.email.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.check_circle,
                0,
                0,
                0
            )
        } else {
            holder.binding.email.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.cancel_icon,
                0,
                0,
                0
            )
        }

        holder.binding.status.apply {
            text = hiringDataList.status
            isSelected = true
        }
        holder.binding.followDate.text = hiringDataList.date_follow

        if (hiringDataList.remaining_call_count != null) {
            holder.binding.callRemaining.text = hiringDataList.remaining_call_count.toString()
        }
        holder.binding.emailRemaining.text = hiringDataList.remaining_email_count.toString()
        holder.binding.smsRemaining.text = hiringDataList.remaining_sms_count.toString()
        holder.binding.whatsappRemaining.text = hiringDataList.remaining_whatsapp_count.toString()
        holder.binding.whatsappRemaining.text = hiringDataList.remaining_whatsapp_count.toString()

        Glide.with(context).load(hiringDataList.pic).placeholder(R.drawable.account_circle)
            .into(holder.binding.profileImage)

        holder.binding.remark.text = hiringDataList.remarks

        holder.binding.callRemaining.setOnClickListener {
            if(hiringDataList.hireID  == null) return@setOnClickListener
            makeCall(hiringDataList.userID, hiringDataList.hireID, hiringDataList.name)
        }

        holder.binding.smsRemaining.setOnClickListener {
            if(hiringDataList.hireID  == null) return@setOnClickListener
            sendCloudMessage(hiringDataList.userID, hiringDataList.hireID, hiringDataList.name)
        }
        holder.binding.whatsappRemaining.setOnClickListener {
            if(hiringDataList.hireID  == null) return@setOnClickListener
            sendWhatsappMessage(hiringDataList.userID, hiringDataList.hireID, hiringDataList.name)
        }

        if (hiringDataList.hireID == 0 || hiringDataList.hireID == null) {
            holder.binding.ll5.visibility = View.GONE
        }

        holder.binding.emailRemaining.setOnClickListener {
            if(hiringDataList.hireID  == null) return@setOnClickListener
            sendEmailMessage(hiringDataList.userID, hiringDataList.hireID, hiringDataList.name)
        }

        holder.binding.morePopMenu.setOnClickListener { view ->
            if(hiringDataList.hireID  == null) return@setOnClickListener
            showPopMenu(view, hiringDataList.hireID, hiringDataList.userID, hiringDataList.name)
        }

    }


    class ViewHolder(val binding: SingleRowDataBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object {
        private val COMPARATOR =
            object : DiffUtil.ItemCallback<HiringDataResponse.ApiData.HiringData>() {
                override fun areItemsTheSame(
                    oldItem: HiringDataResponse.ApiData.HiringData,
                    newItem: HiringDataResponse.ApiData.HiringData
                ): Boolean {
                    return oldItem.userID == newItem.userID
                }

                override fun areContentsTheSame(
                    oldItem: HiringDataResponse.ApiData.HiringData,
                    newItem: HiringDataResponse.ApiData.HiringData
                ): Boolean {
                    return oldItem == newItem
                }

            }
    }
}