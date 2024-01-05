package com.margdarshakendra.margdarshak.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.margdarshakendra.margdarshak.databinding.SingleScheduledStudyRowBinding
import com.margdarshakendra.margdarshak.models.GetOrganisedStudySchedulesResponse

class OrganisedStudyAdapter: ListAdapter<GetOrganisedStudySchedulesResponse.TableData, OrganisedStudyAdapter.ViewHolder>(DiffUtilBack()) {

    private var adapterCallback: AdapterCallback? = null

    fun setAdapterCallback(callback: AdapterCallback) {
        adapterCallback = callback
    }

    class DiffUtilBack : DiffUtil.ItemCallback<GetOrganisedStudySchedulesResponse.TableData>() {
        override fun areItemsTheSame(
            oldItem: GetOrganisedStudySchedulesResponse.TableData,
            newItem: GetOrganisedStudySchedulesResponse.TableData
        ): Boolean {
            return oldItem.scheduleID == newItem.scheduleID
        }

        override fun areContentsTheSame(
            oldItem: GetOrganisedStudySchedulesResponse.TableData,
            newItem: GetOrganisedStudySchedulesResponse.TableData
        ): Boolean {
            return oldItem == newItem
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleScheduledStudyRowBinding =
            SingleScheduledStudyRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(singleScheduledStudyRowBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tableData = getItem(position)

        holder.binding.action.text = (holder.absoluteAdapterPosition.plus(1)).toString()
        holder.binding.courseOrExam.text = tableData.course
        holder.binding.subject.text = tableData.subject
        holder.binding.lessonsArray.text = tableData.lesson_names
        holder.binding.startAndEndDate.text = tableData.date_start.plus(", ${tableData.date_finish}")
        holder.binding.days.text = tableData.study_days
        holder.binding.time.text = tableData.study_time

        holder.binding.editSchedule.setOnClickListener {
            adapterCallback?.onScheduleEditSelected(tableData.scheduleID)
        }

       // adapterCallback?.onScheduleEditSelected(tableData.scheduleID)


    }


    class ViewHolder(val binding: SingleScheduledStudyRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }


    interface AdapterCallback {
        fun onScheduleEditSelected(scheduleID : Int)
    }
}

