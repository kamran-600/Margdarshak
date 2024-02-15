package com.margdarshakendra.margdarshak.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.bumptech.glide.Glide
import com.margdarshakendra.margdarshak.R
import com.margdarshakendra.margdarshak.databinding.SingleRowDataBinding
import com.margdarshakendra.margdarshak.models.CounsellingDataResponse
import com.margdarshakendra.margdarshak.models.HiringDataResponse
import kotlinx.coroutines.Dispatchers

class CounsellingDataRecAdapter(
    private val context: Context,
    private val makeCall: (Int, String) -> Unit,
    private val sendCloudMessage: (Int, String) -> Unit,
    private val sendWhatsappMessage: (Int, String) -> Unit,
    private val sendEmailMessage: (Int, String) -> Unit,
    private val showPopMenu: (View, Int, String) -> Unit
) :
    PagingDataAdapter<HiringDataResponse.ApiData.HiringData, CounsellingDataRecAdapter.ViewHolder>(COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleRowDataBinding =
            SingleRowDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(singleRowDataBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val counsellingData = getItem(position) ?: return
        holder.binding.position.visibility = View.GONE
        holder.binding.lateDays.visibility = View.GONE

        val address = listOf(counsellingData.district, counsellingData.state, counsellingData.pincode)

        holder.binding.address.apply {
            text = address.filterNotNull().joinToString(", ")
            isSelected = true
        }
        holder.binding.name.apply {
            text = counsellingData.name
            isSelected = true
        }

        holder.binding.dataAdvisorName.text = counsellingData.advisor_name
        holder.binding.followDate.text = counsellingData.date_follow

        holder.binding.remark.text = counsellingData.remarks
        holder.binding.status.apply {
            text = counsellingData.status
            isSelected = true
        }

        "log(${counsellingData.log_count})".also { holder.binding.logs.text = it }
        if (counsellingData.usertype == "S") {
            "Student".also { holder.binding.userType.text = it }
        } else "Job Seeker".also { holder.binding.userType.text = it }

        holder.binding.mobile.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        holder.binding.email.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        if (counsellingData.mobile_verified == "true") {
            holder.binding.mobile.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.check_circle,
                0,
                0,
                0
            )
        }
        else{
            holder.binding.mobile.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.cancel_icon,
                0,
                0,
                0
            )
        }

        if (counsellingData.email_verified == "true") {
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

        if (counsellingData.usertype == "S") {
            "Student".also { holder.binding.userType.text = it }
        } else "".also { holder.binding.userType.text = it }


        if (counsellingData.remaining_call_count != null) {
            holder.binding.callRemaining.text = counsellingData.remaining_call_count.toString()
        }
        holder.binding.emailRemaining.text = counsellingData.remaining_email_count.toString()
        holder.binding.smsRemaining.text = counsellingData.remaining_sms_count.toString()
        holder.binding.whatsappRemaining.text = counsellingData.remaining_whatsapp_count.toString()
        holder.binding.whatsappRemaining.text = counsellingData.remaining_whatsapp_count.toString()
/*

        Glide.with(context).load(counsellingData.pic).placeholder(R.drawable.account_circle).into(holder.binding.profileImage)
*/

        holder.binding.profileImage.load(counsellingData.pic) {
            placeholder(R.drawable.account_circle)
            error(R.drawable.account_circle)
            crossfade(true)
            transformations(CircleCropTransformation())
        }
        holder.binding.callRemaining.setOnClickListener {
            makeCall(counsellingData.userID, counsellingData.name)
        }

        holder.binding.smsRemaining.setOnClickListener {
            sendCloudMessage(counsellingData.userID, counsellingData.name)
        }
        holder.binding.whatsappRemaining.setOnClickListener {
            sendWhatsappMessage(counsellingData.userID, counsellingData.name)
        }

        holder.binding.emailRemaining.setOnClickListener {
            sendEmailMessage(counsellingData.userID, counsellingData.name)
        }

        holder.binding.morePopMenu.setOnClickListener { view ->
            showPopMenu(view, counsellingData.userID, counsellingData.name)
        }

        /*val dateFormat = SimpleDateFormat("dd-MM-yy hh:mm a", Locale.getDefault())

        var convertedDate: Date? = null
        try {
            if(counsellingData.date_follow  != null){
                convertedDate =
                    dateFormat.parse(counsellingData.date_follow)
            }
        } catch (e: ParseException) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }

        if(convertedDate != null){

            Log.d(TAG, convertedDate.toString() )
            Log.d(TAG, convertedDate.time.toString() )
            val calendar = Calendar.getInstance()
            calendar.time = convertedDate
            calendar.add(Calendar.MINUTE, -10)
            val timeInMillis = calendar.timeInMillis
            // Log.d(TAG, dateFormat.format(date) )
            Log.d(TAG, calendar.time.toString() )
            Log.d(TAG, timeInMillis.toString() )

            *//*val cal = Calendar.getInstance()
            cal.add(Calendar.MINUTE, 1)
            Log.d(TAG, cal.time.toString() )*//*

            scheduleNotification(timeInMillis)

        }
*/


    }

    class ViewHolder(val binding: SingleRowDataBinding) :
        RecyclerView.ViewHolder(binding.root)


    companion object{
        private val COMPARATOR = object : DiffUtil.ItemCallback<HiringDataResponse.ApiData.HiringData>(){
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