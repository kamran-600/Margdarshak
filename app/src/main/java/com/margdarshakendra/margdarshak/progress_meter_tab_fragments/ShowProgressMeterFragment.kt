package com.margdarshakendra.margdarshak.progress_meter_tab_fragments


import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.margdarshakendra.margdarshak.R
import com.margdarshakendra.margdarshak.databinding.FragmentShowProgressMeterBinding
import com.margdarshakendra.margdarshak.models.GetOrganiserLessonsRequest
import com.margdarshakendra.margdarshak.models.GetOrganiserSubjectsRequest
import com.margdarshakendra.margdarshak.models.GetOrganiserUtilRequest
import com.margdarshakendra.margdarshak.models.ProgressMeterDataResponse
import com.margdarshakendra.margdarshak.utils.Constants.TAG
import com.margdarshakendra.margdarshak.utils.NetworkResult
import com.margdarshakendra.margdarshak.viewmodels.ShowProgressMeterViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.ceil

@AndroidEntryPoint
class ShowProgressMeterFragment : Fragment() {

    private lateinit var binding: FragmentShowProgressMeterBinding
    private val showProgressMeterViewModel by viewModels<ShowProgressMeterViewModel>()
    private var courseId = 0
    private lateinit var progressMeterSubjectMap: HashMap<Int, ProgressMeterDataResponse.Data.ResultSubjectWise>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShowProgressMeterBinding.inflate(inflater, container, false)

        getProgressMeterCourses()

        progressMeterSubjectMap = HashMap()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showProgressMeterViewModel.showProgressMeterCoursesLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data.toString())
                    val courseMap = LinkedHashMap<String, Int>()

                    for (i in it.data!!.interactiveCourses) {
                        courseMap[i.course] = i.courseID
                    }
                    val coursesListAdapter = ArrayAdapter(
                        requireContext(), android.R.layout.simple_spinner_dropdown_item,
                        courseMap.keys.toList()
                    )
                    coursesListAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                    binding.courseAutoCompleteTextView.setAdapter(coursesListAdapter)

                    binding.courseAutoCompleteTextView.setOnItemClickListener { _, _, _, _ ->
                        courseId =
                            courseMap[binding.courseAutoCompleteTextView.text.toString()]!!
                        binding.subjectAutoCompleteTextView.setAdapter(null)
                        binding.subjectAutoCompleteTextView.text = null
                        getProgressMeterSubjects(courseId)
                        getProgressMeterData(courseId)

                        binding.speedView.visibility = View.GONE

                        Log.d(TAG, courseId.toString())
                    }
                    binding.courseAutoCompleteTextView.setOnClickListener {
                        binding.courseAutoCompleteTextView.showDropDown()
                    }
                    binding.courseAutoCompleteTextView.setOnFocusChangeListener { v, hasFocus ->
                        if (hasFocus) {
                            binding.courseAutoCompleteTextView.showDropDown()
                        }
                    }
                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {}
            }
        }

        showProgressMeterViewModel.showProgressMeterSubjectsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data.toString())

                    val subjectMap = LinkedHashMap<String, Int>()

                    for (i in it.data!!.subjects) {
                        subjectMap[i.subject] = i.subjectID
                    }
                    val subjectsListAdapter = ArrayAdapter(
                        requireContext(), android.R.layout.simple_spinner_dropdown_item,
                        subjectMap.keys.toList()
                    )
                    subjectsListAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                    binding.subjectAutoCompleteTextView.setAdapter(subjectsListAdapter)

                    binding.subjectAutoCompleteTextView.setOnItemClickListener { _, _, _, _ ->
                        val subjectId =
                            subjectMap[binding.subjectAutoCompleteTextView.text.toString()]!!
                        Log.d(TAG, subjectId.toString())

                        getProgressMarksAndTime(
                            GetOrganiserLessonsRequest(
                                courseId.toString(),
                                "progress_chart",
                                subjectId.toString()
                            )
                        )

                        binding.marksProgressText.visibility = View.GONE
                        binding.timeProgressText.visibility = View.GONE
                        binding.progressMeter.visibility = View.GONE


                    }

                    binding.subjectAutoCompleteTextView.setOnClickListener {
                        binding.subjectAutoCompleteTextView.showDropDown()
                    }

                    binding.subjectAutoCompleteTextView.setOnFocusChangeListener { v, hasFocus ->
                        if (hasFocus) {
                            binding.subjectAutoCompleteTextView.showDropDown()
                        }
                    }
                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {}
            }
        }

        showProgressMeterViewModel.progressMeterLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {

                    Log.d(TAG, it.data!!.toString())
                    if (!it.data.success) return@observe

                    binding.speedView.speedTo(0F, 0)
                    binding.pieChartView.clear()


                    val marksData = it.data.data.data
                    if (marksData.marks.isNullOrEmpty() || marksData.t_marks.isNullOrEmpty()) return@observe

                    var marksPercentage =
                        (marksData.marks.toFloat() / marksData.t_marks.toFloat()).times(100)
                    marksPercentage = ceil(marksPercentage)
                    Log.d(TAG, marksPercentage.toString())
                    binding.speedView.visibility = View.VISIBLE
                    binding.speedView.speedTo(marksPercentage)


                    val pieEntries = mutableListOf<PieEntry>()
                    val subjectMarksList = mutableListOf<Float>()

                    for (subject in it.data.data.result_subject_wise) {

                        if (subject.marks.isNullOrEmpty() || subject.t_marks.isNullOrEmpty()) {
                            continue
                        }

                        val subjectMarksPercentage = (subject.marks.toFloat()
                            .div(subject.t_marks.toFloat())).times(100)

                        Log.d(TAG, subjectMarksPercentage.toString())
                        subjectMarksList.add(subjectMarksPercentage)

                        pieEntries.add(PieEntry(subjectMarksPercentage, subject.subject))

                    }

                    val pieDataSet = MyPieDataset(pieEntries, "Subjects", subjectMarksList)
                    pieDataSet.setAutomaticallyDisableSliceSpacing(false)
                    pieDataSet.setColors(
                        ColorTemplate.rgb("#e74c3c"),
                        ColorTemplate.rgb("#2ecc71"),
                        ColorTemplate.rgb("#f1c40f")
                    )  //  red, green , yellow
                    pieDataSet.sliceSpace = 1F
                    pieDataSet.valueTextSize = 10F
                    val pieData = PieData(pieDataSet)

                    binding.pieChartView.apply {
                        description.text = "Subject Wise Progress"
                        animateY(1500)
                        data = pieData
                        setDrawEntryLabels(true)
                        setEntryLabelColor(Color.BLACK)
                        setEntryLabelTextSize(11F)
                        transparentCircleRadius = 0F

                    }

                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {}
            }
        }

        showProgressMeterViewModel.progressMarksAndTimeLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {

                    Log.d(TAG, it.data!!.toString())
                    if (!it.data.success) return@observe

                    binding.marksProgressText.visibility = View.VISIBLE
                    binding.timeProgressText.visibility = View.VISIBLE
                    binding.progressMeter.visibility = View.VISIBLE

                    binding.marksProgress.progress = 0
                    binding.timeProgress.progress = 0
                    binding.marksProgressText.text = "Marks : 0 %"
                    binding.timeProgressText.text = "Time Spent : 0 %"

                    if(it.data.data!!.data != null && it.data.data.data!!.marks != null && it.data.data.data.t_marks != null){

                        val achieveMarks = it.data.data.data.marks!!.toFloat()
                        val totalMarks = it.data.data.data.t_marks.toFloat()
                        val marksPercentage  = ceil((achieveMarks / totalMarks) * 100).toInt()

                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                        if(it.data.data.data.date_start != null && it.data.data.data.date_finish != null) {
                            val dateStart = dateFormat.parse(it.data.data.data.date_start)
                            val dateFinish = dateFormat.parse(it.data.data.data.date_finish)
                            if (dateStart != null && dateFinish != null) {
                                val timeDifference = (dateFinish.time) - (dateStart.time)
                                val totalDaysOrganised =
                                    ceil((timeDifference / (1000 * 3600 * 24)).toDouble()).toInt()

                                val today = Calendar.getInstance().time
                                val daysSpent = today.time - dateStart.time
                                val totalDaysSpent =
                                    ceil(daysSpent / (1000 * 3600 * 24).toDouble()).toInt()

                                val daysRem = totalDaysOrganised - totalDaysSpent

                                var timePercentage = 0
                                if (totalDaysOrganised != 0) {
                                    timePercentage =
                                        ceil((daysRem.toDouble() / totalDaysOrganised.toDouble()) * 100).toInt()
                                }

                                Log.d(TAG, "marks Percent : $marksPercentage")
                                Log.d(TAG, "time Percent : $timePercentage")

                                if(marksPercentage < 40){
                                    binding.marksProgress.progressTintList = ColorStateList.valueOf(Color.RED)
                                }
                                else if( marksPercentage < 70){
                                    binding.marksProgress.progressTintList = ColorStateList.valueOf(Color.YELLOW)
                                }
                                else{
                                    binding.marksProgress.progressTintList = ColorStateList.valueOf(Color.GREEN)
                                }
                                binding.marksProgressText.text = "Marks : $marksPercentage %"
                                binding.marksProgress.setProgress(marksPercentage,true)

                                if(timePercentage < 40){
                                    binding.timeProgress.progressTintList = ColorStateList.valueOf(Color.RED)
                                }
                                else if( marksPercentage < 70){
                                    binding.timeProgress.progressTintList = ColorStateList.valueOf(Color.YELLOW)
                                }
                                else{
                                    binding.timeProgress.progressTintList = ColorStateList.valueOf(Color.GREEN)
                                }

                                binding.timeProgressText.text = "Time Spent : $timePercentage %"
                                binding.timeProgress.setProgress(timePercentage, true)

                            }
                        }

                    }

                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {}
            }
        }

    }

    private fun getProgressMarksAndTime(getOrganiserLessonsRequest: GetOrganiserLessonsRequest) {
        showProgressMeterViewModel.getProgressMarksAndTime(getOrganiserLessonsRequest)
    }

    private fun getProgressMeterSubjects(courseId: Int) {
        showProgressMeterViewModel.getProgressMeterSubjectsRequest(
            GetOrganiserSubjectsRequest(
                courseId.toString(),
                "interactiveSubjects"
            )
        )
    }

    private fun getProgressMeterData(courseId: Int) {
        showProgressMeterViewModel.getProgressMeterData(
            GetOrganiserSubjectsRequest(
                courseId.toString(),
                "progress_meter"
            )
        )
    }

    private fun getProgressMeterCourses() {
        showProgressMeterViewModel.getProgressMeterCoursesRequest(GetOrganiserUtilRequest("interactiveCourses"))
    }


    class MyPieDataset internal constructor(
        yVals: List<PieEntry?>?,
        label: String?,
        private val credits: List<Float>
    ) :
        PieDataSet(yVals, label) {
        override fun getEntryIndex(e: PieEntry?): Int {
            return -1
        }

        override fun getColor(index: Int): Int {
            val c = credits[index]
            return if (c < 40) {
                mColors[0]
            } else if (c >= 40 && c < 70) {
                mColors[2]
            } else {
                mColors[1]
            }
        }
    }


}