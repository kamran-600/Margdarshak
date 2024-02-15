package com.margdarshakendra.margdarshak.progress_meter_tab_fragments

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.margdarshakendra.margdarshak.R
import com.margdarshakendra.margdarshak.databinding.FragmentCompareBinding
import com.margdarshakendra.margdarshak.models.GetComparisonDataRequest
import com.margdarshakendra.margdarshak.models.GetOrganiserLessonsRequest
import com.margdarshakendra.margdarshak.models.GetOrganiserSubjectsRequest
import com.margdarshakendra.margdarshak.models.GetOrganiserUtilRequest
import com.margdarshakendra.margdarshak.utils.Constants.TAG
import com.margdarshakendra.margdarshak.utils.NetworkResult
import com.margdarshakendra.margdarshak.viewmodels.CompareViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CompareFragment : Fragment() {
    private var compareByLessonBtnClicked = false
    private var compareBySubjectBtnClicked = false
    private lateinit var binding: FragmentCompareBinding
    private val compareViewModel by viewModels<CompareViewModel>()
    private var courseId = 0
    private var subjectId = 0
    private var lessonId = 0
    private lateinit var lessonMap: HashMap<String, Int>
    private lateinit var competitorList: MutableList<String>
    private lateinit var competitorUserIdList: MutableList<Int>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCompareBinding.inflate(inflater, container, false)

        getCompareCourses()

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lessonMap = HashMap()
        competitorList = mutableListOf()
        competitorUserIdList = mutableListOf()

        compareViewModel.compareCoursesLiveData.observe(viewLifecycleOwner) {
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
                        binding.lessonAutoCompleteTextView.setAdapter(null)
                        binding.lessonAutoCompleteTextView.text = null
                        getCompareSubjects(courseId)
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

        compareViewModel.compareSubjectsLiveData.observe(viewLifecycleOwner) {
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
                        subjectId =
                            subjectMap[binding.subjectAutoCompleteTextView.text.toString()]!!
                        binding.lessonAutoCompleteTextView.setAdapter(null)
                        binding.lessonAutoCompleteTextView.text = null
                        getCompareLessons(subjectId)
                        Log.d(TAG, subjectId.toString())
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

        compareViewModel.compareLessonsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data.toString())

                    lessonMap.clear()

                    for (i in it.data!!.lessons) {
                        lessonMap[i.lesson] = i.lessonID
                    }
                    val lessonsListAdapter = ArrayAdapter(
                        requireContext(), android.R.layout.simple_spinner_dropdown_item,
                        lessonMap.keys.toList()
                    )
                    lessonsListAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                    binding.lessonAutoCompleteTextView.setAdapter(lessonsListAdapter)

                    binding.lessonAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
                        lessonId =
                            lessonMap[binding.lessonAutoCompleteTextView.text.toString()]!!
                        //getMcqResults(lessonId)
                        Log.d(TAG, lessonId.toString())

                    }

                    binding.lessonAutoCompleteTextView.setOnClickListener {
                        binding.lessonAutoCompleteTextView.showDropDown()
                    }

                    binding.lessonAutoCompleteTextView.setOnFocusChangeListener { v, hasFocus ->
                        if (hasFocus) {
                            binding.lessonAutoCompleteTextView.showDropDown()
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

        binding.email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(
                emailText: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (emailText.isNullOrEmpty()) {
                    binding.searchedUserName.visibility = View.GONE
                    return
                }

                if (isGmailAddress(emailText)) {
                    Log.d(TAG, emailText.toString())
                    compareViewModel.searchEmail(emailText.toString())
                } else {
                    binding.searchedUserName.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        compareViewModel.emailSearchLiveData.observe(viewLifecycleOwner) {
            binding.spinKit.visibility = View.GONE
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.toString())
                    binding.searchedUserName.visibility = View.VISIBLE
                    if (it.data.user == null) {
                        binding.searchedUserName.text = "Email Not Found !"
                        binding.searchedUserName.setTextColor(Color.RED)
                        return@observe
                    }
                    binding.addCompetitorBtn.visibility = View.VISIBLE
                    binding.searchedUserName.setTextColor(
                        resources.getColor(
                            R.color.parrotGreen,
                            null
                        )
                    )

                    binding.searchedUserName.text = it.data.user.name
                    competitorUserIdList.add(it.data.user.userID)

                    binding.addCompetitorBtn.setOnClickListener {
                        if (TextUtils.isEmpty(binding.searchedUserName.text)) return@setOnClickListener
                        if (competitorList.contains(binding.searchedUserName.text)) return@setOnClickListener

                        binding.competitors.visibility = View.VISIBLE
                        binding.addCompetitorBtn.visibility = View.GONE
                        binding.email.text = null
                        binding.barCharView.clear()

                        competitorList.add(binding.searchedUserName.text.toString())
                        Log.d(TAG, competitorList.toString())

                        binding.competitors.text = competitorList.joinToString(System.lineSeparator())
                    }

                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {
                    binding.spinKit.visibility = View.VISIBLE
                }
            }
        }


        binding.compareBySubjectBtn.setOnClickListener {
            compareBySubjectBtnClicked = true
            compareByLessonBtnClicked = false
            val competitorsString = competitorUserIdList.joinToString(",")
            Log.d(TAG, competitorsString)
            if(courseId == 0){
                Toast.makeText(requireContext(), "Please Select Course", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(subjectId == 0){
                Toast.makeText(requireContext(), "Please Select Subject", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(competitorsString.isEmpty()){
                Toast.makeText(requireContext(), "Please Add Any Comptitor", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val getComparisonDataRequest = GetComparisonDataRequest("comparison_by_subject", competitorsString, courseId.toString(), subjectId.toString() )
            Log.d(TAG, getComparisonDataRequest.toString())
            compareViewModel.getComparisonDataRequest(getComparisonDataRequest)
        }

        binding.compareByLessonBtn.setOnClickListener {
            compareByLessonBtnClicked = true
            compareBySubjectBtnClicked = false
            val competitorsString = competitorUserIdList.joinToString(",")
            Log.d(TAG, competitorsString)
            if(courseId == 0){
                Toast.makeText(requireContext(), "Please Select Course", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(subjectId == 0){
                Toast.makeText(requireContext(), "Please Select Subject", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(lessonId == 0){
                Toast.makeText(requireContext(), "Please Select lesson", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(competitorsString.isEmpty()){
                Toast.makeText(requireContext(), "Please Add Any Competitor", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val getComparisonDataRequest = GetComparisonDataRequest("comparison_by_lesson", competitorsString, courseId.toString(), subjectId.toString(), lessonId.toString() )
            Log.d(TAG, getComparisonDataRequest.toString())
            compareViewModel.getComparisonDataRequest(getComparisonDataRequest)
        }

        compareViewModel.comparisonLiveData.observe(viewLifecycleOwner){
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.toString())

                    binding.barCharView.clear()

                    val competitorsList = it.data.data!!.data

                    val entries = ArrayList<BarEntry>()
                    val marksList = ArrayList<Float>()
                    val xAxisLabelList = ArrayList<String>()
                    xAxisLabelList.add("")

                    competitorsList.forEachIndexed { index, competitor ->
                        if(competitor.avg_marks != null){
                            entries.add(BarEntry((index + 1).toFloat(), competitor.avg_marks.toFloat()))
                            xAxisLabelList.add(competitor.name)
                            marksList.add(competitor.avg_marks.toFloat())
                        }
                        else{
                            entries.add(BarEntry((index + 1).toFloat(), 0f))
                            xAxisLabelList.add(competitor.name)
                            marksList.add(0f)
                        }
                    }

                    Log.d(TAG, xAxisLabelList.size.toString())
                    Log.d(TAG, xAxisLabelList.toString())


                    val dataSet = PerformanceFragment.MyBarDataset(entries, "Average Marks", marksList)
                    //dataSet.setColors(*ColorTemplate.MATERIAL_COLORS)  ColorTemplate.rgb("#f1c40f")// yellow
                    dataSet.setColors(ColorTemplate.rgb("#e74c3c"), ColorTemplate.rgb("#2ecc71"))  //  red, green
                    dataSet.valueTextColor = Color.BLACK
                    dataSet.valueTextSize = 10f

                    val barData = BarData(dataSet)

                    barData.barWidth = 0.6f
                    barData.isHighlightEnabled = true

                    var type = ""
                    if(compareByLessonBtnClicked){
                        type = "Lesson"
                    }
                    if(compareBySubjectBtnClicked){
                        type = "Subject"
                    }


                    binding.barCharView.apply {
                        description.text = "Avg Marks By $type"
                        animateY(1500)
                        data = barData
                        zoomOut()
                        xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabelList)
                        xAxis.granularity = 1f
                        xAxis.labelRotationAngle = -45f
                        xAxis.position = XAxis.XAxisPosition.BOTTOM
                        setFitBars(true)
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


    private fun getCompareCourses() {
        compareViewModel.getCompareCoursesRequest(GetOrganiserUtilRequest("interactiveCourses"))
    }

    private fun getCompareLessons(subjectId: Int) {
        compareViewModel.getCompareLessonsRequest(
            GetOrganiserLessonsRequest(
                courseId.toString(),
                "get_lesson",
                subjectId.toString()
            )
        )
    }

    private fun getCompareSubjects(courseId: Int) {
        compareViewModel.getCompareSubjectsRequest(
            GetOrganiserSubjectsRequest(
                courseId.toString(),
                "interactiveSubjects"
            )
        )
    }

    private fun isGmailAddress(email: CharSequence): Boolean {
        val regex = "^[a-zA-Z0-9._%+-]+@gmail\\.com$".toRegex()
        return regex.matches(email)
    }

}