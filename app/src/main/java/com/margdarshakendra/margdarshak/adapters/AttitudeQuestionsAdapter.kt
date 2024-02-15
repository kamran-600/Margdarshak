package com.margdarshakendra.margdarshak.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.margdarshakendra.margdarshak.R
import com.margdarshakendra.margdarshak.databinding.SingleRowAttitudeQuestionBinding
import com.margdarshakendra.margdarshak.models.AttitudeQuestionsResponse
import com.margdarshakendra.margdarshak.utils.Constants.TAG

class AttitudeQuestionsAdapter(private val context: Context) :
    ListAdapter<AttitudeQuestionsResponse.Data, AttitudeQuestionsAdapter.ViewHolder>(DiffUtilBack()) {

    private var adapterCallback: AdapterCallback? = null

    private var updatedPosition = 0

    fun setAdapterCallback(callback: AdapterCallback) {
        adapterCallback = callback
    }
    /*private var itemClickListener: ((AttitudeQuestionsResponse.Data) -> Unit)? = null

    fun setOnItemClickListener(listener: (AttitudeQuestionsResponse.Data) -> Unit) {
        itemClickListener = listener
    }

    fun updateItemPosition(fromPosition: Int, updatedPosition: Int) {
        if (updatedPosition < itemCount) {
            val items = currentList.toMutableList()
            val movedItem = items.removeAt(fromPosition)
            items.add(updatedPosition, movedItem)
            Log.d(TAG, "$fromPosition to ${updatedPosition}")

            submitList(items)
        }
    }*/

    class DiffUtilBack : DiffUtil.ItemCallback<AttitudeQuestionsResponse.Data>() {
        override fun areItemsTheSame(
            oldItem: AttitudeQuestionsResponse.Data,
            newItem: AttitudeQuestionsResponse.Data
        ): Boolean {
            return oldItem.attiquestID == newItem.attiquestID
        }

        override fun areContentsTheSame(
            oldItem: AttitudeQuestionsResponse.Data,
            newItem: AttitudeQuestionsResponse.Data
        ): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleRowAttitudeQuestionBinding =
            SingleRowAttitudeQuestionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(singleRowAttitudeQuestionBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val attitudeQuestionsData = getItem(position)

        /*holder.bind(questionsData) {
            itemClickListener?.invoke(it)
        }
*/
        holder.binding.question.text =
            HtmlCompat.fromHtml(attitudeQuestionsData.attiquest, HtmlCompat.FROM_HTML_MODE_LEGACY)

        if(!attitudeQuestionsData.isSelected){
            holder.binding.root.setBackgroundColor(context.getColor(R.color.white))
            updatedPosition = 0
            holder.binding.serialNo.text = "?"
        }else {
            holder.binding.root.setBackgroundColor(context.getColor(R.color.parrotGreen))
        }

        holder.binding.root.setOnClickListener {
            //adapterCallback?.onItemClicked(position)
            //updateItemPosition(position)

            if (updatedPosition < itemCount && !attitudeQuestionsData.isSelected) {
                adapterCallback?.onItemClicked(attitudeQuestionsData.attiquestID, attitudeQuestionsData.attiquest)
                attitudeQuestionsData.isSelected  = true
                val items = currentList.toMutableList()
                val movedItem = items.removeAt(holder.absoluteAdapterPosition)
                items.add(updatedPosition, movedItem)
               // Log.d(TAG, "${holder.absoluteAdapterPosition} to $updatedPosition")
                holder.binding.root.setBackgroundColor(context.getColor(R.color.parrotGreen))
                holder.binding.serialNo.text = "${++updatedPosition}"

                submitList(items)

            }

        }


    }

    class ViewHolder(val binding: SingleRowAttitudeQuestionBinding) :
        RecyclerView.ViewHolder(binding.root){
        /*fun bind(item: AttitudeQuestionsResponse.Data, clickListener: (AttitudeQuestionsResponse.Data) -> Unit) {
            binding.root.setOnClickListener {
                clickListener(item)
            }
        }*/
        }


    interface AdapterCallback {
        fun onItemClicked(attiquestID : Int, attiquest : String)
    }

}