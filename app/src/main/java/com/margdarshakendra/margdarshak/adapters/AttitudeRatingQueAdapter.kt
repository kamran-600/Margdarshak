package com.margdarshakendra.margdarshak.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.margdarshakendra.margdarshak.databinding.SingleRowRatingPhaseQuestionBinding
import com.margdarshakendra.margdarshak.models.AttitudeRatingQuesResponse

class AttitudeRatingQueAdapter : ListAdapter<AttitudeRatingQuesResponse.Question, AttitudeRatingQueAdapter.ViewHolder>(DiffUtilBack()) {

    private var adapterCallback: AdapterCallback? = null

    fun setAdapterCallback(callback: AdapterCallback) {
        adapterCallback = callback
    }

    class DiffUtilBack : DiffUtil.ItemCallback<AttitudeRatingQuesResponse.Question>() {
        override fun areItemsTheSame(
            oldItem: AttitudeRatingQuesResponse.Question,
            newItem: AttitudeRatingQuesResponse.Question
        ): Boolean {
            return oldItem.attiquestID == newItem.attiquestID
        }

        override fun areContentsTheSame(
            oldItem: AttitudeRatingQuesResponse.Question,
            newItem: AttitudeRatingQuesResponse.Question
        ): Boolean {
            return oldItem == newItem
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleRowRatingPhaseQuestionBinding =
            SingleRowRatingPhaseQuestionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(singleRowRatingPhaseQuestionBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val attitudeRatingQuestionsData = getItem(position)

        holder.binding.question.text =
            HtmlCompat.fromHtml(
                attitudeRatingQuestionsData.attiquest,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )

        holder.binding.serialNo.text = attitudeRatingQuestionsData.attiquestID.toString()

        adapterCallback?.onRadioButtonSelected(holder.absoluteAdapterPosition + 1, null)


        holder.binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            var selectedRadioBtn: String? = null
            when (checkedId) {
                holder.binding.yesBtn.id -> selectedRadioBtn = "Y"
                holder.binding.noBtn.id -> selectedRadioBtn = "N"
            }

            if (selectedRadioBtn != null) {
                adapterCallback?.onRadioButtonSelected(
                    holder.absoluteAdapterPosition + 1,
                    selectedRadioBtn
                )
            }
        }


    }


    class ViewHolder(val binding: SingleRowRatingPhaseQuestionBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }


    interface AdapterCallback {
        fun onRadioButtonSelected(serialNo: Int, selectedRadioBtn: String?)
    }
}

