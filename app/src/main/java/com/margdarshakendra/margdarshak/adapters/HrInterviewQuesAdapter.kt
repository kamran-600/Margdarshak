package com.margdarshakendra.margdarshak.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.margdarshakendra.margdarshak.R
import com.margdarshakendra.margdarshak.databinding.SingleRowHrInterviewQueBinding
import com.margdarshakendra.margdarshak.models.HrInterviewQuesResponse

class HrInterviewQuesAdapter(
    private val context: Context,
    private val onAskBtnClick: (Int) -> Unit,
    private val onResetAskBtn: (Int) -> Unit,
    private val saveQuesRank: (Int, Int?) -> Unit
) : ListAdapter<HrInterviewQuesResponse.Question, HrInterviewQuesAdapter.ViewHolder>(DiffUtilBack()) {

    private var adapterCallback: AdapterCallback? = null

    fun setAdapterCallback(callback: AdapterCallback) {
        adapterCallback = callback
    }

    class DiffUtilBack : DiffUtil.ItemCallback<HrInterviewQuesResponse.Question>() {
        override fun areItemsTheSame(
            oldItem: HrInterviewQuesResponse.Question,
            newItem: HrInterviewQuesResponse.Question
        ): Boolean {
            return oldItem.hrquestID == newItem.hrquestID
        }

        override fun areContentsTheSame(
            oldItem: HrInterviewQuesResponse.Question,
            newItem: HrInterviewQuesResponse.Question
        ): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleRowHrInterviewQueBinding =
            SingleRowHrInterviewQueBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(singleRowHrInterviewQueBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val hrInterviewQues = getItem(position)

        holder.binding.serialNo.text = "${hrInterviewQues.hrquestID}".plus(".")

        holder.binding.question.text =
            HtmlCompat.fromHtml(hrInterviewQues.question, HtmlCompat.FROM_HTML_MODE_LEGACY).trim()

        holder.binding.AskBtn.setOnClickListener {
            holder.binding.AskBtn.visibility = View.GONE
            holder.binding.rankAutoCompleteTextView.visibility = View.VISIBLE
            onAskBtnClick(hrInterviewQues.hrquestID)
        }

        setRankAdapter(holder, context)

        holder.binding.resetAskBtn.setOnClickListener {
            if(holder.binding.rankAutoCompleteTextView.visibility == View.GONE){
                return@setOnClickListener
            }
            else{
                onResetAskBtn(hrInterviewQues.hrquestID)
                holder.binding.rankAutoCompleteTextView.text = null
                saveQuesRank(hrInterviewQues.hrquestID, null)
                adapterCallback?.onUpdateAnyQuestionRankedMap(hrInterviewQues.hrquestID, false)
            }
        }

        holder.binding.rankAutoCompleteTextView.setOnItemClickListener { _, _, _, _ ->
            saveQuesRank(hrInterviewQues.hrquestID, holder.binding.rankAutoCompleteTextView.text.toString().toInt())
            adapterCallback?.onUpdateAnyQuestionRankedMap(hrInterviewQues.hrquestID, true)
        }

    }

    private fun setRankAdapter(holder: ViewHolder, context: Context) {

        val rankList = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")

        val rankListAdapter = ArrayAdapter(
            context, android.R.layout.simple_spinner_dropdown_item,
            rankList
        )
        rankListAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        holder.binding.rankAutoCompleteTextView.setAdapter(rankListAdapter)

        holder.binding.rankAutoCompleteTextView.setOnClickListener {
            holder.binding.rankAutoCompleteTextView.showDropDown()
        }
        holder.binding.rankAutoCompleteTextView.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) holder.binding.rankAutoCompleteTextView.showDropDown()
        }

    }

    class ViewHolder(val binding: SingleRowHrInterviewQueBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface AdapterCallback {
        fun onUpdateAnyQuestionRankedMap(hrquestID: Int, status :Boolean)
    }

}