package com.margdarshakendra.margdarshak.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.margdarshakendra.margdarshak.databinding.SingleRowTeacherDescBinding
import com.margdarshakendra.margdarshak.models.InteractiveTeachersResponse

class InteractiveClassAdapter(
    private val openPdf: (String?) -> Unit,
    private val goToInteractiveTestFragment: (Int) -> Unit,
    private val openVideo: (String?) -> Unit
) :
    ListAdapter<InteractiveTeachersResponse.Data.TableData, InteractiveClassAdapter.ViewHolder>(
        DiffUtilBack()
    ) {

    private var adapterCallback: OrganisedStudyAdapter.AdapterCallback? = null

    fun setAdapterCallback(callback: OrganisedStudyAdapter.AdapterCallback) {
        adapterCallback = callback
    }

    class DiffUtilBack : DiffUtil.ItemCallback<InteractiveTeachersResponse.Data.TableData>() {
        override fun areItemsTheSame(
            oldItem: InteractiveTeachersResponse.Data.TableData,
            newItem: InteractiveTeachersResponse.Data.TableData
        ): Boolean {
            return oldItem.lesson_id == newItem.lesson_id
        }

        override fun areContentsTheSame(
            oldItem: InteractiveTeachersResponse.Data.TableData,
            newItem: InteractiveTeachersResponse.Data.TableData
        ): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleRowTeacherDescBinding =
            SingleRowTeacherDescBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(singleRowTeacherDescBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val interactiveData = getItem(position)

        holder.binding.lesson.text = interactiveData.lesson_name
        holder.binding.marksPercentage.text = "Marks : ${interactiveData.percentage} %"
        holder.binding.teacherName.text = interactiveData.teacher
        holder.binding.liveClassLink.text = interactiveData.meet_link
        holder.binding.liveClassTime.text = interactiveData.class_time
        holder.binding.attempts.text = "Attempts : ${interactiveData.test_counts}"

        if(interactiveData.teacher_pic != null){
            Glide.with(holder.binding.image.context).load(interactiveData.teacher_pic).into(holder.binding.image)
        }

        holder.binding.pdfFile.setOnClickListener {
            if (interactiveData.contents.isEmpty()){
                openPdf(null)
            }
            else openPdf(interactiveData.contents[0])
        }

        holder.binding.giveTestBtn.setOnClickListener {
            goToInteractiveTestFragment(interactiveData.lesson_id)
        }

        holder.binding.playIcon.setOnClickListener {
            if(interactiveData.videos.isEmpty()){
                openVideo(null)
            }
            else openVideo(interactiveData.videos[0])
        }


    }





    class ViewHolder(val binding: SingleRowTeacherDescBinding) :
        RecyclerView.ViewHolder(binding.root)


}