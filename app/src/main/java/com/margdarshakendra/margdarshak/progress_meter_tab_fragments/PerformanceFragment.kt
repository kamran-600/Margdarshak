package com.margdarshakendra.margdarshak.progress_meter_tab_fragments


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
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.margdarshakendra.margdarshak.R
import com.margdarshakendra.margdarshak.databinding.FragmentPerformanceBinding
import com.margdarshakendra.margdarshak.models.GetOrganiserLessonsRequest
import com.margdarshakendra.margdarshak.models.GetOrganiserSubjectsRequest
import com.margdarshakendra.margdarshak.models.GetOrganiserUtilRequest
import com.margdarshakendra.margdarshak.utils.Constants.TAG
import com.margdarshakendra.margdarshak.utils.NetworkResult
import com.margdarshakendra.margdarshak.viewmodels.PerformanceViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PerformanceFragment : Fragment() {

    private lateinit var binding: FragmentPerformanceBinding
    private val performanceViewModel by viewModels<PerformanceViewModel>()
    private var courseId = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPerformanceBinding.inflate(inflater, container, false)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getPerformanceCourses()

        getPerformanceSubjectWise()

        performanceViewModel.performanceCoursesLiveData.observe(viewLifecycleOwner) {
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
                        getPerformanceSubjects(courseId)
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

        performanceViewModel.performanceSubjectsLiveData.observe(viewLifecycleOwner) {
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
                        getPerformanceLessonWise(subjectId)
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

        performanceViewModel.subjectWisePerformanceLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.toString())

                    /*val barChart = AnyChart.bar()
                    APIlib.getInstance().setActiveAnyChartView(binding.chartView)
                    barChart.animation(true)

                    barChart.title("Subject Wise Performance")

                    barChart.isVertical(true)

                    val data: MutableList<DataEntry> = ArrayList()
                    for (i in it.data.performance_subject_wise) {
                        data.add(ValueDataEntry(i.subject, i.marks.toFloat()))
                    }

                    barChart.bar(data)
                    binding.chartView.setChart(barChart)
*/
                    val subjectWisePerformanceList = it.data.performance_subject_wise

                    val entries = ArrayList<BarEntry>()
                    val marksList = ArrayList<Float>()
                    val xAxisLabelList = ArrayList<String>()
                    xAxisLabelList.add("")
                    subjectWisePerformanceList.forEachIndexed { index, performanceSubjectWise ->
                        if(performanceSubjectWise.marks != null){
                            entries.add(BarEntry((index + 1).toFloat(), performanceSubjectWise.marks.toFloat()))
                            xAxisLabelList.add(performanceSubjectWise.subject)
                            marksList.add(performanceSubjectWise.marks.toFloat())
                        }
                        else{
                            entries.add(BarEntry((index + 1).toFloat(), 0f))
                            xAxisLabelList.add(performanceSubjectWise.subject)
                            marksList.add(0f)
                        }
                    }

                    Log.d(TAG, xAxisLabelList.size.toString())


                    val dataSet = MyBarDataset(entries, "Marks", marksList)
                    //dataSet.setColors(*ColorTemplate.MATERIAL_COLORS)ColorTemplate.rgb("#f1c40f")// yellow
                    dataSet.setColors(ColorTemplate.rgb("#e74c3c"), ColorTemplate.rgb("#2ecc71"))  // green , red
                    dataSet.valueTextColor = Color.BLACK
                    dataSet.valueTextSize = 10f

                    val barData = BarData(dataSet)

                    barData.barWidth = 0.6f
                    barData.isHighlightEnabled = true

                   /* val formatter: ValueFormatter = object : ValueFormatter() {
                        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                            return xAxisLabelList[value.toInt()]
                        }
                    }*/

                    binding.chartView.apply {
                        description.text = "Subject Wise Performance"
                        animateY(1500)
                        data = barData
                        zoomOut()
                        xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabelList)
                        xAxis.granularity = 1f
                        xAxis.labelRotationAngle = -45f
                        xAxis.position = XAxis.XAxisPosition.BOTTOM
                        setFitBars(true)
                    }

                    /*while(!binding.chartView.isFullyZoomedOut){
                        binding.chartView.zoomOut()
                    }*/


                    /*val list = arrayListOf<BarEntry>()

                    list.add(BarEntry())

                    val barDataSet = BarDataSet(list, "Pollutants")

                    barDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS,255)
                    barDataSet.valueTextColor = Color.BLACK
                    barDataSet.barBorderColor = Color.BLACK

                    barDataSet.barBorderWidth = 1f

                    binding.chartView.data = BarData(barDataSet)

                    binding.chartView.apply {
                        description.text = "Marks"
                        animateY(1000)
                    }

                    val quarters = arrayOf("", "CO", "NH3", "NO" , "NO2", "PM10", "O3", "PM2.5", "SO2")
                    val formatter : ValueFormatter = object : ValueFormatter(){
                        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                            return quarters[value.toInt()]
                        }
                    }

                    val xAxis = binding.barChart.xAxis
                    xAxis.valueFormatter = formatter

*/

                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {}
            }
        }


        performanceViewModel.lessonWisePerformanceLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.toString())
                    /* binding.chartView.setChart(null)
                     binding.chartView.setZoomEnabled(false)
                     APIlib.getInstance().setActiveAnyChartView(binding.chartView)

                     val barChart = AnyChart.bar()
                     barChart.animation(true)
                     barChart.title("Lesson Wise Performance")

                     barChart.isVertical(true)

                     val data: MutableList<DataEntry> = ArrayList()
                     for (i in it.data.performance_lesson_wise) {
                         data.add(ValueDataEntry(i.lesson, i.marks.toDouble()))
                     }

                     barChart.bar(data)
                     binding.chartView.setChart(barChart)
 */


                    val lessonWisePerformanceList = it.data.performance_lesson_wise

                    val entries = ArrayList<BarEntry>()
                    val marksList = ArrayList<Float>()
                    val xAxisLabelList = ArrayList<String>()
                    xAxisLabelList.add("")

                    lessonWisePerformanceList.forEachIndexed { index, performanceLessonWise ->
                        if(performanceLessonWise.marks != null){
                            entries.add(BarEntry((index + 1).toFloat(), performanceLessonWise.marks.toFloat()))
                            xAxisLabelList.add(performanceLessonWise.lesson)
                            marksList.add(performanceLessonWise.marks.toFloat())
                        }
                        else{
                            entries.add(BarEntry((index + 1).toFloat(), 0f))
                            xAxisLabelList.add(performanceLessonWise.lesson)
                            marksList.add(0f)
                        }
                    }

                    Log.d(TAG, xAxisLabelList.size.toString())
                    Log.d(TAG, xAxisLabelList.toString())


                    val dataSet = MyBarDataset(entries, "Marks" , marksList)
                    //dataSet.setColors(*ColorTemplate.MATERIAL_COLORS)  ColorTemplate.rgb("#f1c40f")// yellow
                    dataSet.setColors(ColorTemplate.rgb("#e74c3c"), ColorTemplate.rgb("#2ecc71"))  //  red, green
                    dataSet.valueTextColor = Color.BLACK
                    dataSet.valueTextSize = 10f

                    val barData = BarData(dataSet)

                    barData.barWidth = 0.6f
                    barData.isHighlightEnabled = true
/*
                    val formatter: ValueFormatter = object : ValueFormatter() {
                        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                            Log.d(TAG, xAxisLabelList[value.toInt()])
                            return xAxisLabelList[value.toInt()]
                        }
                    }*/

                    binding.chartView.apply {
                        description.text = "Lesson Wise Performance"
                        animateY(1500)
                        data = barData
                        zoomOut()
                        xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabelList)
                        xAxis.granularity = 1f
                        xAxis.labelRotationAngle = -45f
                        xAxis.position = XAxis.XAxisPosition.BOTTOM
                        setFitBars(true)
                    }

                    /*while(!binding.chartView.isFullyZoomedOut){
                        binding.chartView.zoomOut()
                    }*/


                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {}
            }
        }

    }

    private fun getPerformanceSubjectWise() {
        performanceViewModel.getSubjectWisePerformance(GetOrganiserUtilRequest("performance_subject_wise"))
    }

    private fun getPerformanceLessonWise(subjectId: Int) {
        performanceViewModel.getLessonWisePerformance(
            GetOrganiserLessonsRequest(
                courseId.toString(),
                "performance_lesson_wise",
                subjectId.toString()
            )
        )
    }

    private fun getPerformanceSubjects(courseId: Int) {
        performanceViewModel.getPerformanceSubjectsRequest(
            GetOrganiserSubjectsRequest(
                courseId.toString(),
                "interactiveSubjects"
            )
        )
    }

    private fun getPerformanceCourses() {
        performanceViewModel.getPerformanceCoursesRequest(GetOrganiserUtilRequest("interactiveCourses"))
    }


    class MyBarDataset internal constructor(
        yVals: List<BarEntry?>?,
        label: String?,
        private val credits: List<Float>
    ) :
        BarDataSet(yVals, label) {
        override fun getEntryIndex(e: BarEntry?): Int {
            return -1
        }

        override fun getColor(index: Int): Int {
            val c = credits[index]
            return if (c < 0) {
                Log.d(TAG, "$c Red")
                mColors[0]
            }  else {
                Log.d(TAG, "$c Green")
                mColors[1]
            }
        }
    }



}