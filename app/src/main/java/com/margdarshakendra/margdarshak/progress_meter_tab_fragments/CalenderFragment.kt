package com.margdarshakendra.margdarshak.progress_meter_tab_fragments

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.applandeo.materialcalendarview.CalendarDay
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.getDrawableText
import com.margdarshakendra.margdarshak.R
import com.margdarshakendra.margdarshak.databinding.FragmentCalenderBinding
import com.margdarshakendra.margdarshak.models.GetOrganiserUtilRequest
import com.margdarshakendra.margdarshak.utils.Constants.TAG
import com.margdarshakendra.margdarshak.utils.NetworkResult
import com.margdarshakendra.margdarshak.viewmodels.CalenderViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class CalenderFragment : Fragment() {

    private lateinit var binding : FragmentCalenderBinding

    private val calenderViewModel by viewModels<CalenderViewModel>()

      override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       binding = FragmentCalenderBinding.inflate(inflater, container, false)

        calenderViewModel.getCalenderSchedules(GetOrganiserUtilRequest("calender"))

       return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calenderViewModel.calenderSchedulesLiveData.observe(viewLifecycleOwner){
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.toString())
                    /*val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    val events  = mutableListOf<EventDay>()

                    for(lesson in it.data.lessons){

                        val lessonText = requireContext().getDrawableText(lesson.subject, Typeface.DEFAULT, R.color.upperBlue, 12)
                        Log.d(TAG, lessonText.toString())
                        val startDate = Calendar.getInstance()
                            startDate.time = dateFormat.parse(lesson.date_start)!!

                        val endDate = Calendar.getInstance()
                            endDate.time = dateFormat.parse(lesson.date_finish)!!

                        val currentDate = startDate.clone() as Calendar
                        Log.d(TAG, currentDate.time.toString())
                        while (!currentDate.after(endDate)) {

                            events.add(EventDay(currentDate, lessonText))
                            // Move to the next date
                            currentDate.add(Calendar.DAY_OF_MONTH, 1)
                            Log.d(TAG, currentDate.time.toString())
                        }

                    }*/

                  //  Log.d(TAG, events.toString())


                  //  binding.calenderView.setEvents(events)
                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {}
            }
        }
    }

}