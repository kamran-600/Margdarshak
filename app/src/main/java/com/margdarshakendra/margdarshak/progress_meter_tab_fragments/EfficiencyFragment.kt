package com.margdarshakendra.margdarshak.progress_meter_tab_fragments


import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.margdarshakendra.margdarshak.databinding.FragmentEfficiencyBinding
import com.margdarshakendra.margdarshak.models.GetOrganiserLessonsRequest
import com.margdarshakendra.margdarshak.models.GetOrganiserSubjectsRequest
import com.margdarshakendra.margdarshak.models.GetOrganiserUtilRequest
import com.margdarshakendra.margdarshak.models.McqResultsRequest
import com.margdarshakendra.margdarshak.utils.Constants.TAG
import com.margdarshakendra.margdarshak.utils.NetworkResult
import com.margdarshakendra.margdarshak.viewmodels.EfficiencyViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.SortedMap


@AndroidEntryPoint
class EfficiencyFragment : Fragment() {
    private lateinit var binding: FragmentEfficiencyBinding

    private val efficiencyViewModel by viewModels<EfficiencyViewModel>()

    private var courseId = 0

    private lateinit var lessonMap: HashMap<String, Int>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEfficiencyBinding.inflate(inflater, container, false)

        getEfficiencyCourses()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lessonMap = HashMap()

        efficiencyViewModel.efficiencyCoursesLiveData.observe(viewLifecycleOwner) {
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
                    coursesListAdapter.setDropDownViewResource(com.margdarshakendra.margdarshak.R.layout.spinner_dropdown_item)
                    binding.courseAutoCompleteTextView.setAdapter(coursesListAdapter)

                    binding.courseAutoCompleteTextView.setOnItemClickListener { _, _, _, _ ->
                        courseId =
                            courseMap[binding.courseAutoCompleteTextView.text.toString()]!!
                        binding.subjectAutoCompleteTextView.setAdapter(null)
                        binding.subjectAutoCompleteTextView.text = null
                        binding.lessonAutoCompleteTextView.setAdapter(null)
                        binding.lessonAutoCompleteTextView.text = null
                        getEfficiencySubjects(courseId)
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

        efficiencyViewModel.efficiencySubjectsLiveData.observe(viewLifecycleOwner) {
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
                    subjectsListAdapter.setDropDownViewResource(com.margdarshakendra.margdarshak.R.layout.spinner_dropdown_item)
                    binding.subjectAutoCompleteTextView.setAdapter(subjectsListAdapter)

                    binding.subjectAutoCompleteTextView.setOnItemClickListener { _, _, _, _ ->
                        val subjectId =
                            subjectMap[binding.subjectAutoCompleteTextView.text.toString()]!!
                        binding.lessonAutoCompleteTextView.setAdapter(null)
                        binding.lessonAutoCompleteTextView.text = null
                        getEfficiencyLessons(subjectId)
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

        efficiencyViewModel.efficiencyLessonsLiveData.observe(viewLifecycleOwner) {
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
                    lessonsListAdapter.setDropDownViewResource(com.margdarshakendra.margdarshak.R.layout.spinner_dropdown_item)
                    binding.lessonAutoCompleteTextView.setAdapter(lessonsListAdapter)

                    binding.lessonAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
                        val lessonId =
                            lessonMap[binding.lessonAutoCompleteTextView.text.toString()]!!
                        getMcqResults(lessonId)
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

        efficiencyViewModel.mcqResultsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.toString())

                    val dayAndMarksHashMap = LinkedHashMap<String, Float>()

                    for (i in it.data.mcq_results) {
                        if (i.marks.isNullOrBlank()) {
                            dayAndMarksHashMap.merge(
                                getDayFromDate(i.edate.split(" ")[0]),
                                0f
                            ) { oldValue, newValue -> oldValue + newValue }
                        } else dayAndMarksHashMap.merge(
                            getDayFromDate(i.edate.split(" ")[0]),
                            i.marks.toFloat()
                        ) { oldValue, newValue -> oldValue + newValue }
                    }

                    val updatedMap = addMissingDays(dayAndMarksHashMap)

                    Log.d(TAG, dayAndMarksHashMap.toString())

                    // val efficiencyList : MutableCollection<Float> = dateAndMarksHashMap.values

                    val entries = ArrayList<Entry>()

                    val xAxisLabelList = ArrayList<String>()
                    xAxisLabelList.add("")
                    updatedMap.values.forEachIndexed { index, mark ->
                        entries.add(Entry((index + 1).toFloat(), mark))
                    }
                    xAxisLabelList.addAll(updatedMap.keys.toList() as ArrayList<String>)
                    Log.d(TAG, xAxisLabelList.size.toString())
                    Log.d(TAG, xAxisLabelList.toString())
                    Log.d(TAG, updatedMap.toString())


                    val dataSet = LineDataSet(entries, "Marks")
                    //dataSet.setColors(*ColorTemplate.MATERIAL_COLORS)
                    dataSet.setColors(
                        ContextCompat.getColor(
                            requireContext(),
                            com.margdarshakendra.margdarshak.R.color.upperBlue
                        )
                    )
                    dataSet.valueTextColor = Color.BLACK
                    dataSet.valueTextSize = 10f

                    val lineData = LineData(dataSet)
                    lineData.isHighlightEnabled = true


                    binding.chartView.apply {
                        description.text = "Total Marks Per Day"
                        animateY(1500)
                        data = lineData
                        zoomOut()
                        xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabelList)
                        xAxis.granularity = 1f
                        xAxis.labelRotationAngle = -45f
                        xAxis.position = XAxis.XAxisPosition.BOTTOM
                    }
                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {
                }
            }
        }


    }

    private fun addMissingDays(originalMap: LinkedHashMap<String, Float>): SortedMap<String, Float> {
        val daysOfWeek =
            listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

        // Create a copy of the original map to avoid modifying it directly
        val updatedMap = HashMap(originalMap)

        // Check for missing days and add them with a default mark (0)
        for (day in daysOfWeek) {
            if (!updatedMap.containsKey(day)) {
                updatedMap[day] = 0f
            }
        }

        // Create a sorted map based on the custom order of days
        val sortedMap = updatedMap.toSortedMap(compareBy { daysOfWeek.indexOf(it) })

        return sortedMap
    }


    private fun getDayFromDate(dateString: String): String {
        return try {
            // Parse the input date string
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(dateString)

            // Create a Calendar instance and set the parsed date
            val calendar = Calendar.getInstance()
            if (date == null) {
                Toast.makeText(requireContext(), "Date is null", Toast.LENGTH_SHORT).show()
                return "date is null"
            }
            calendar.time = date
            // Get the day of the week as an integer (Sunday = 1, Monday = 2, ..., Saturday = 7)
            val dayOfWeek: Int = calendar.get(Calendar.DAY_OF_WEEK)

            // Convert the integer to a string representation of the day
            val daysOfWeek = arrayOf(
                "",
                "Sunday",
                "Monday",
                "Tuesday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday"
            )
            daysOfWeek[dayOfWeek]
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Invalid Date", Toast.LENGTH_SHORT).show()
            "Invalid Date"
        }
    }

    private fun getMcqResults(lessonId: Int) {
        efficiencyViewModel.getMcqResults(McqResultsRequest(lessonId, "mcq_results"))
    }

    private fun getEfficiencySubjects(courseId: Int) {
        efficiencyViewModel.getEfficiencySubjectsRequest(
            GetOrganiserSubjectsRequest(
                courseId.toString(),
                "interactiveSubjects"
            )
        )
    }

    private fun getEfficiencyCourses() {
        efficiencyViewModel.getEfficiencyCoursesRequest(GetOrganiserUtilRequest("interactiveCourses"))
    }

    private fun getEfficiencyLessons(subjectId: Int) {
        efficiencyViewModel.getEfficiencyLessonsRequest(
            GetOrganiserLessonsRequest(
                courseId.toString(),
                "get_lesson",
                subjectId.toString()
            )
        )
    }


}