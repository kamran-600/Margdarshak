package com.margdarshakendra.margdarshak.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.margdarshakendra.margdarshak.databinding.SingleRowQuestionBinding
import com.margdarshakendra.margdarshak.models.QuestionsResponse
import com.margdarshakendra.margdarshak.utils.Constants.TAG

class AptitudeQuestionsAdapter : ListAdapter<QuestionsResponse.Question, AptitudeQuestionsAdapter.ViewHolder>(DiffUtilBack()) {

    private var adapterCallback: AdapterCallback? = null

    fun setAdapterCallback(callback: AdapterCallback) {
        adapterCallback = callback
    }

    class DiffUtilBack : DiffUtil.ItemCallback<QuestionsResponse.Question>() {
        override fun areItemsTheSame(
            oldItem: QuestionsResponse.Question,
            newItem: QuestionsResponse.Question
        ): Boolean {
            return oldItem.aptiquestID == newItem.aptiquestID
        }

        override fun areContentsTheSame(
            oldItem: QuestionsResponse.Question,
            newItem: QuestionsResponse.Question
        ): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleRowQuestionBinding =
            SingleRowQuestionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(singleRowQuestionBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val questionsData = getItem(position)

        holder.binding.question.text = HtmlCompat.fromHtml(questionsData.aptiquest, HtmlCompat.FROM_HTML_MODE_LEGACY )
        holder.binding.serialNo.text = questionsData.aptiquestID.toString()
        if(questionsData.ans != null){
            when (questionsData.ans) {
                "L" -> holder.binding.radioGroup.check(holder.binding.likeBtn.id)
                "D" -> holder.binding.radioGroup.check(holder.binding.dislikeBtn.id)
                "U" -> holder.binding.radioGroup.check(holder.binding.notSureBtn.id)
            }
            adapterCallback?.onRadioButtonSelected(holder.absoluteAdapterPosition+1, questionsData.ans)

        }
        else {
            adapterCallback?.onRadioButtonSelected(holder.absoluteAdapterPosition+1, null)

        }


        holder.binding.radioGroup.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener{
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                var selectedRadioBtn : String? = null
                when(checkedId){
                    holder.binding.likeBtn.id -> selectedRadioBtn = "L"
                    holder.binding.dislikeBtn.id -> selectedRadioBtn = "D"
                    holder.binding.notSureBtn.id -> selectedRadioBtn = "U"
                }

                if (selectedRadioBtn != null) {
                    adapterCallback?.onRadioButtonSelected(holder.absoluteAdapterPosition+1, selectedRadioBtn)
                }
            }

            })

    }

    class ViewHolder(val binding: SingleRowQuestionBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface AdapterCallback {
        fun onRadioButtonSelected(serialNo: Int, selectedRadioBtn: String?)
    }

}