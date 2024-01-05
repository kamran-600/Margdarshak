package com.margdarshakendra.margdarshak.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.margdarshakendra.margdarshak.databinding.SingleRowResultBinding
import com.margdarshakendra.margdarshak.models.McqResultsResponse

class ResultAdapter :
    ListAdapter<McqResultsResponse.McqResult, ResultAdapter.ViewHolder>(DiffUtilBack()) {

    private var adapterCallback: AdapterCallback? = null

    fun setAdapterCallback(callback: AdapterCallback) {
        adapterCallback = callback
    }

    class DiffUtilBack : DiffUtil.ItemCallback<McqResultsResponse.McqResult>() {
        override fun areItemsTheSame(
            oldItem: McqResultsResponse.McqResult,
            newItem: McqResultsResponse.McqResult
        ): Boolean {
            return oldItem.resultID == newItem.resultID
        }

        override fun areContentsTheSame(
            oldItem: McqResultsResponse.McqResult,
            newItem: McqResultsResponse.McqResult
        ): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleRowResultBinding =
            SingleRowResultBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(singleRowResultBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tableData = getItem(position)

        tableData.edate.let {
            holder.binding.testDate.text = it
        }
        if(tableData.ques_attempt != null){
            holder.binding.Attempted.text = tableData.ques_attempt
            tableData.ans_correct.let {rightAnsCount ->
                holder.binding.rightFlag.text = rightAnsCount
                holder.binding.wrongFlag.text = (tableData.ques_attempt.toInt() - rightAnsCount.toInt()).toString()
            }
        }
        tableData.ques_total.let {
            holder.binding.totalMarks.text = it
            holder.binding.totalQuestions.text = it
        }
        if(tableData.marks != null){
            holder.binding.markReceived.text = tableData.marks

            holder.binding.percentage.text = ((tableData.marks.toFloat().div(tableData.ques_total.toFloat())).times(
                100
            )).toString().plus(" %")
        }

        // adapterCallback?.onScheduleEditSelected(tableData.scheduleID)



    }


    class ViewHolder(val binding: SingleRowResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }


    interface AdapterCallback {
        fun onScheduleEditSelected(scheduleID: Int)
    }
}
