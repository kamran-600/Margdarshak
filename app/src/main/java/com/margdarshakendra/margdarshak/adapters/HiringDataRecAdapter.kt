package com.margdarshakendra.margdarshak.adapters


import android.content.Context
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
import com.margdarshakendra.margdarshak.databinding.SingleRowClientdataBinding
import com.margdarshakendra.margdarshak.models.HiringDataResponse
import com.margdarshakendra.margdarshak.utils.Constants
import com.margdarshakendra.margdarshak.utils.Constants.TAG
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HiringDataRecAdapter(
    private val context: Context,
    private val makeCall: (Int, Int, String) -> Unit,
    private val sendCloudMessage: (Int, Int, String) -> Unit,
    private val sendWhatsappMessage: (Int, Int, String) -> Unit,
    private val sendEmailMessage: (Int, Int, String) -> Unit
) :
    ListAdapter<HiringDataResponse.Data, HiringDataRecAdapter.ViewHolder>(DiffCallBack()) {

    class DiffCallBack : DiffUtil.ItemCallback<HiringDataResponse.Data>() {
        override fun areItemsTheSame(
            oldItem: HiringDataResponse.Data,
            newItem: HiringDataResponse.Data
        ): Boolean {
            return oldItem.userID == newItem.userID
        }

        override fun areContentsTheSame(
            oldItem: HiringDataResponse.Data,
            newItem: HiringDataResponse.Data
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
        val hiringDataList = getItem(position)
        holder.binding.position.text = hiringDataList.position
        "log(${hiringDataList.log_count})".also { holder.binding.logs.text = it }
        if (hiringDataList.usertype == "S") {
            "Student".also { holder.binding.userType.text = it }
        } else "Job Seeker".also { holder.binding.userType.text = it }

        hiringDataList.state.let {
            holder.binding.state.text = it
        }
        hiringDataList.district.let {
            holder.binding.district.text = it
        }
        hiringDataList.pincode.let {
            holder.binding.pincode.text = it
        }
        holder.binding.name.apply {
            text = hiringDataList.name
            isSelected = true
        }
        holder.binding.dataAdvisorName.text = hiringDataList.advisor_name
        //  val checkdrawable = ContextCompat.getDrawable(context, R.drawable.check_circle)
        if (hiringDataList.mobile_verified == "true") {
            holder.binding.mobile.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.check_circle,
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

        if (hiringDataList.pic != null) {
            Glide.with(context).load(hiringDataList.pic).into(holder.binding.profileImage)
        }

        holder.binding.remark.text = hiringDataList.remarks

        holder.binding.callRemaining.setOnClickListener {
            makeCall(hiringDataList.userID, hiringDataList.hireID, hiringDataList.name)
        }

        holder.binding.smsRemaining.setOnClickListener {
            sendCloudMessage(hiringDataList.userID, hiringDataList.hireID, hiringDataList.name)
        }
        holder.binding.whatsappRemaining.setOnClickListener {
            sendWhatsappMessage(hiringDataList.userID, hiringDataList.hireID, hiringDataList.name)
        }

        if (hiringDataList.hireID == 0) {
            holder.binding.ll5.visibility = View.GONE
        }

        holder.binding.emailRemaining.setOnClickListener {
            sendEmailMessage(hiringDataList.userID, hiringDataList.hireID, hiringDataList.name)
        }

        /*val dateFormat = SimpleDateFormat("dd-MM-yy hh:mm a", Locale.getDefault())

        var convertedDate: Date? = null
        try {
            if (hiringDataList.date_follow != "") {
                convertedDate = dateFormat.parse(hiringDataList.date_follow)
            }
        } catch (e: ParseException) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }

        if (convertedDate != null) {

            *//*Log.d(Constants.TAG, convertedDate.toString())
            Log.d(Constants.TAG, convertedDate.time.toString())
            val calendar = Calendar.getInstance()
            calendar.time = convertedDate
            calendar.add(Calendar.MINUTE, -10)
            val timeInMillis = calendar.timeInMillis
            // Log.d(TAG, dateFormat.format(date) )
            Log.d(Constants.TAG, calendar.time.toString())
            Log.d(Constants.TAG, timeInMillis.toString())*//*
            val cal = Calendar.getInstance()
            cal.add(Calendar.MINUTE, position+1)
            Log.d(TAG, cal.time.toString() )

            scheduleNotification(cal.timeInMillis)

        }*/


    }

    class ViewHolder(val binding: SingleRowClientdataBinding) :
        RecyclerView.ViewHolder(binding.root)

}