package com.margdarshakendra.margdarshak.progress_meter_tab_fragments

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.MultiAutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import cn.pedant.SweetAlert.SweetAlertDialog
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.applySkeleton
import com.google.android.material.checkbox.MaterialCheckBox.STATE_CHECKED
import com.google.android.material.checkbox.MaterialCheckBox.STATE_UNCHECKED
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.margdarshakendra.margdarshak.R
import com.margdarshakendra.margdarshak.adapters.OrganisedStudyAdapter
import com.margdarshakendra.margdarshak.databinding.FragmentStudyOrganiserBinding
import com.margdarshakendra.margdarshak.models.EditOrganisedStudyScheduleResponse
import com.margdarshakendra.margdarshak.models.GetOrganiserLessonsRequest
import com.margdarshakendra.margdarshak.models.GetOrganiserStudyTimeRequest
import com.margdarshakendra.margdarshak.models.GetOrganiserSubjectsRequest
import com.margdarshakendra.margdarshak.models.GetOrganiserUtilRequest
import com.margdarshakendra.margdarshak.models.OrganisedStudyScheduleRequest
import com.margdarshakendra.margdarshak.utils.Constants.TAG
import com.margdarshakendra.margdarshak.utils.NetworkResult
import com.margdarshakendra.margdarshak.viewmodels.StudyOrganiserViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class StudyOrganiserFragment : Fragment(), OrganisedStudyAdapter.AdapterCallback {

    private lateinit var lessonMap: LinkedHashMap<String, Int>
    private lateinit var courseMap: LinkedHashMap<String, Int>
    private lateinit var languageMap: LinkedHashMap<String, Int>
    private lateinit var weightageMap: LinkedHashMap<String, String>
    private lateinit var subjectMap: LinkedHashMap<String, Int>
    private lateinit var binding: FragmentStudyOrganiserBinding
    private lateinit var holidayDateSet: LinkedHashSet<String>
    private var editableData: EditOrganisedStudyScheduleResponse.EditableData? = null
    private var courseId = 0
    private var languageId = 0
    private var subjectId = 0
    private var weightage = ""
    private var totalStudyTime = 0L
    private var numOfSunday = 0L
    private var numOfMonday = 0L
    private var numOfTuesday = 0L
    private var numOfWednesday = 0L
    private var numOfThursday = 0L
    private var numOfFriday = 0L
    private var numOfSaturday = 0L

    private val studyOrganiserViewModel by viewModels<StudyOrganiserViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudyOrganiserBinding.inflate(inflater, container, false)

        getOrganiserUtils()
        studyOrganiserViewModel.getOrganisedStudySchedules()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        holidayDateSet = LinkedHashSet()

        lessonMap = LinkedHashMap()
        courseMap = LinkedHashMap()
        languageMap = LinkedHashMap()
        weightageMap = LinkedHashMap()
        subjectMap = LinkedHashMap()

        handleRadioButtonCheck()

        setStartAndEndDate()
        setHolidayDates()

        bindObservers()


    }


    private fun bindObservers() {

        studyOrganiserViewModel.organiserCoursesLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data.toString())
                    //binding.subjectAutoCompleteTextView.text = null
                    courseMap.clear()

                    for (i in it.data!!.courses) {
                        courseMap[i.course] = i.courseID
                    }
                    val coursesListAdapter = ArrayAdapter(
                        requireContext(), android.R.layout.simple_spinner_dropdown_item,
                        courseMap.keys.toList()
                    )
                    coursesListAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                    binding.courseAutoCompleteTextView.setAdapter(coursesListAdapter)
                    //Log.d(TAG, it.data.toString())


                    binding.courseAutoCompleteTextView.setOnItemClickListener { _, _, _, _ ->
                        courseId = courseMap[binding.courseAutoCompleteTextView.text.toString()]!!
                        binding.subjectAutoCompleteTextView.setAdapter(null)
                        binding.subjectAutoCompleteTextView.text = null
                        binding.lessonMultiAutoCompleteTextView.setAdapter(null)
                        binding.lessonMultiAutoCompleteTextView.text = null
                        binding.weightageAutoCompleteTextView.text = null
                        binding.hour.text = null
                        binding.minutes.text = null
                        totalStudyTime = 0L
                        binding.startDate.text = null
                        binding.endDate.text = null
                        holidayDateSet.clear()
                        binding.holidayDates.text = null

                        binding.allCheck.checkedState = STATE_UNCHECKED
                        binding.sundayCheck.checkedState = STATE_UNCHECKED
                        binding.mondayCheck.checkedState = STATE_UNCHECKED
                        binding.tuesdayCheck.checkedState = STATE_UNCHECKED
                        binding.wednesdayCheck.checkedState = STATE_UNCHECKED
                        binding.thursdayCheck.checkedState = STATE_UNCHECKED
                        binding.fridayCheck.checkedState = STATE_UNCHECKED
                        binding.saturdayCheck.checkedState = STATE_UNCHECKED

                        binding.allCheck.isEnabled = false
                        binding.sundayCheck.isEnabled = false
                        binding.mondayCheck.isEnabled = false
                        binding.tuesdayCheck.isEnabled = false
                        binding.wednesdayCheck.isEnabled = false
                        binding.thursdayCheck.isEnabled = false
                        binding.fridayCheck.isEnabled = false
                        binding.saturdayCheck.isEnabled = false
                        getOrganiserSubjects(courseId)
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

        studyOrganiserViewModel.organiserLanguagesLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data.toString())

                    languageMap.clear()

                    for (i in it.data!!.language) {
                        languageMap[i.language] = i.languageID
                    }
                    val languagesListAdapter = ArrayAdapter(
                        requireContext(), android.R.layout.simple_spinner_dropdown_item,
                        languageMap.keys.toList()
                    )
                    languagesListAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                    binding.languageAutoCompleteTextView.setAdapter(languagesListAdapter)
                    //Log.d(TAG, it.data.toString())

                    binding.languageAutoCompleteTextView.setOnItemClickListener { _, _, _, _ ->
                        languageId =
                            languageMap[binding.languageAutoCompleteTextView.text.toString()]!!
                        Log.d(TAG, languageId.toString())
                    }

                    binding.languageAutoCompleteTextView.setOnClickListener {
                        binding.languageAutoCompleteTextView.showDropDown()
                    }

                    binding.languageAutoCompleteTextView.setOnFocusChangeListener { v, hasFocus ->
                        if (hasFocus) {
                            binding.languageAutoCompleteTextView.showDropDown()
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

        studyOrganiserViewModel.organiserWeightagesLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data.toString())
                    weightageMap = LinkedHashMap()

                    weightageMap.clear()

                    for (i in it.data!!.weightage) {
                        weightageMap[i.weightagename] = i.weightage
                    }
                    val weightagesListAdapter = ArrayAdapter(
                        requireContext(), android.R.layout.simple_spinner_dropdown_item,
                        weightageMap.keys.toList()
                    )
                    weightagesListAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                    binding.weightageAutoCompleteTextView.setAdapter(weightagesListAdapter)
                    //Log.d(TAG, it.data.toString())

                    binding.weightageAutoCompleteTextView.setOnItemClickListener { _, _, _, _ ->
                        weightage =
                            weightageMap[binding.weightageAutoCompleteTextView.text.toString()]!!
                        Log.d(TAG, weightage)

                        val lessonsString = binding.lessonMultiAutoCompleteTextView.text?.toString()
                        val lessonNameList =
                            lessonsString?.split(", ")?.filter { item -> item.isNotBlank() }
                        Log.d(TAG, lessonNameList.toString())
                        val lessonIdList = lessonNameList?.mapNotNull { item -> lessonMap[item] }
                        Log.d(TAG, lessonIdList.toString())
                        if (!lessonIdList.isNullOrEmpty()) {
                            val getOrganiserStudyTimeRequest =
                                GetOrganiserStudyTimeRequest(lessonIdList, "studytime", weightage)
                            Log.d(TAG, getOrganiserStudyTimeRequest.toString())
                            getOrganiserStudyTime(getOrganiserStudyTimeRequest)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Please Select Lessons",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }


                    binding.weightageAutoCompleteTextView.setOnClickListener {
                        binding.weightageAutoCompleteTextView.showDropDown()
                    }

                    binding.weightageAutoCompleteTextView.setOnFocusChangeListener { v, hasFocus ->
                        if (hasFocus) {
                            binding.weightageAutoCompleteTextView.showDropDown()
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

        studyOrganiserViewModel.organiserSubjectsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data.toString())

                    subjectMap.clear()

                    for (i in it.data!!.subjects) {
                        subjectMap[i.subject] = i.subjectID
                    }
                    val subjectsListAdapter = ArrayAdapter(
                        requireContext(), android.R.layout.simple_spinner_dropdown_item,
                        subjectMap.keys.toList()
                    )
                    subjectsListAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                    binding.subjectAutoCompleteTextView.setAdapter(subjectsListAdapter)
                    //Log.d(TAG, it.data.toString())

                    if (editableData != null) {
                        subjectId = editableData!!.subjectID.toInt()
                        val subject = getSubjectNameFromId(editableData!!.subjectID.toInt())
                        binding.subjectAutoCompleteTextView.setText(subject, false)
                        binding.subjectAutoCompleteTextView.isEnabled = false
                        studyOrganiserViewModel.getOrganiserLessons(
                            GetOrganiserLessonsRequest(
                                editableData!!.courseID,
                                "lessons",
                                editableData!!.subjectID
                            )
                        )
                    }


                    binding.subjectAutoCompleteTextView.setOnItemClickListener { _, _, _, _ ->
                        subjectId =
                            subjectMap[binding.subjectAutoCompleteTextView.text.toString()]!!

                        binding.lessonMultiAutoCompleteTextView.setAdapter(null)
                        binding.lessonMultiAutoCompleteTextView.text = null
                        binding.weightageAutoCompleteTextView.text = null
                        binding.hour.text = null
                        binding.minutes.text = null
                        totalStudyTime = 0L
                        binding.startDate.text = null
                        binding.endDate.text = null
                        holidayDateSet.clear()
                        binding.holidayDates.text = null

                        binding.allCheck.checkedState = STATE_UNCHECKED
                        binding.sundayCheck.checkedState = STATE_UNCHECKED
                        binding.mondayCheck.checkedState = STATE_UNCHECKED
                        binding.tuesdayCheck.checkedState = STATE_UNCHECKED
                        binding.wednesdayCheck.checkedState = STATE_UNCHECKED
                        binding.thursdayCheck.checkedState = STATE_UNCHECKED
                        binding.fridayCheck.checkedState = STATE_UNCHECKED
                        binding.saturdayCheck.checkedState = STATE_UNCHECKED

                        binding.allCheck.isEnabled = false
                        binding.sundayCheck.isEnabled = false
                        binding.mondayCheck.isEnabled = false
                        binding.tuesdayCheck.isEnabled = false
                        binding.wednesdayCheck.isEnabled = false
                        binding.thursdayCheck.isEnabled = false
                        binding.fridayCheck.isEnabled = false
                        binding.saturdayCheck.isEnabled = false


                        getOrganiserLessons(
                            GetOrganiserLessonsRequest(
                                courseId.toString(),
                                "lessons",
                                subjectId.toString()
                            )
                        )
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

        studyOrganiserViewModel.organiserLessonsLiveData.observe(viewLifecycleOwner) {
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
                    binding.lessonMultiAutoCompleteTextView.setAdapter(lessonsListAdapter)

                    binding.lessonMultiAutoCompleteTextView.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())

                    if (editableData != null) {
                        val lessonNameString =
                            getLessonNamesListFromIds(editableData!!.LessonID).joinToString(", ")
                                .plus(", ")
                        binding.lessonMultiAutoCompleteTextView.setText(null, false)
                        binding.lessonMultiAutoCompleteTextView.append(lessonNameString)
                    }

                    binding.lessonMultiAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
                        val selectedItem = parent.getItemAtPosition(position)
                        doubleSelectToRemoveLesson(selectedItem)
                        Log.d(TAG, selectedItem.toString())

                    }

                    binding.lessonMultiAutoCompleteTextView.setOnClickListener {
                        binding.lessonMultiAutoCompleteTextView.showDropDown()
                    }

                    binding.lessonMultiAutoCompleteTextView.setOnFocusChangeListener { v, hasFocus ->
                        if (hasFocus) {
                            binding.lessonMultiAutoCompleteTextView.showDropDown()
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

        studyOrganiserViewModel.organiserStudyTimeLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.toString())
                    totalStudyTime = it.data.total_study_time.toLong()

                    val hours = TimeUnit.MINUTES.toHours(
                        it.data.total_study_time.toLong()
                    )
                    val minutes = it.data.total_study_time - TimeUnit.HOURS.toMinutes(hours)

                    binding.hour.setText(hours.toString())
                    binding.minutes.setText("${minutes.toInt()}")

                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {}
            }
        }

        val skeleton: Skeleton =
            binding.organisedStudyRecView.applySkeleton(R.layout.single_scheduled_study_row, 3)
        skeleton.showSkeleton()
        studyOrganiserViewModel.organisedStudySchedulesLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    skeleton.showSkeleton()
                    Log.d(TAG, it.data!!.toString())
                    if (it.data.avavilable_schedule_id != null) {
                        binding.innerLL.visibility = View.VISIBLE

                        submitBtnOnClick(it.data.avavilable_schedule_id)
                    } else {
                        binding.innerLL.visibility = View.GONE
                    }

                    val organisedStudyAdapter = OrganisedStudyAdapter()
                    organisedStudyAdapter.setAdapterCallback(this)
                    organisedStudyAdapter.submitList(it.data.table_data)
                    binding.organisedStudyRecView.setHasFixedSize(true)
                    binding.organisedStudyRecView.adapter = organisedStudyAdapter
                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {}
            }
        }

        studyOrganiserViewModel.scheduleOrganisedStudyLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.toString())
                    Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT).show()
                    studyOrganiserViewModel.getOrganisedStudySchedules()
                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {}
            }
        }

        studyOrganiserViewModel.editScheduledOrganisedStudyLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.toString())
                    editableData = it.data.editable_data
                    binding.root.smoothScrollTo(0,0)
                    binding.innerLL.visibility = View.VISIBLE

                    binding.startDate.setText(editableData!!.date_start)
                    binding.endDate.setText(editableData!!.date_finish)
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val startDate = dateFormat.parse(binding.startDate.text.toString())!!
                    val endDate = dateFormat.parse(binding.endDate.text.toString())!!

                    numOfSunday = calculateDaysBetween(startDate, endDate, Calendar.SUNDAY)
                    numOfMonday = calculateDaysBetween(startDate, endDate, Calendar.MONDAY)
                    numOfTuesday = calculateDaysBetween(startDate, endDate, Calendar.TUESDAY)
                    numOfWednesday = calculateDaysBetween(startDate, endDate, Calendar.WEDNESDAY)
                    numOfThursday = calculateDaysBetween(startDate, endDate, Calendar.THURSDAY)
                    numOfFriday = calculateDaysBetween(startDate, endDate, Calendar.FRIDAY)
                    numOfSaturday = calculateDaysBetween(startDate, endDate, Calendar.SATURDAY)

                    binding.holidayDates.text = null
                    holidayDateSet.clear()
                    val dateLeaveString = editableData!!.date_leave
                    val holidayDateList =
                        dateLeaveString?.split(", ")?.filter { item -> item.isNotBlank() }
                    holidayDateList?.let { it1 -> holidayDateSet.addAll(it1) }
                    val holidayDateText = TextUtils.join(", ", holidayDateSet)

                    binding.holidayDates.setText(holidayDateText)
                    totalStudyTime = editableData!!.study_time.toLong()

                    courseId = editableData!!.courseID.toInt()
                    val course = getCourseNameFromId(courseId)
                    binding.courseAutoCompleteTextView.setText(course, false)
                    binding.courseAutoCompleteTextView.isEnabled = false
                    studyOrganiserViewModel.getOrganiserSubjects(
                        GetOrganiserSubjectsRequest(
                            editableData!!.courseID,
                            "subjects"
                        )
                    )

                    languageId = editableData!!.medium.toInt()
                    val language = getLanguageFromId(languageId)
                    binding.languageAutoCompleteTextView.setText(language, false)

                    weightage = editableData!!.weightage
                    val weightageName = getWeightageNameFromId(weightage)
                    binding.weightageAutoCompleteTextView.setText(weightageName, false)

                    setCheckedDays(editableData!!.study_days)

                    submitBtnOnClick(it.data.editable_data.scheduleID)

                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {}
            }
        }

    }

    private fun submitBtnOnClick(availableScheduleId: Any) {
        binding.submitBtn.setOnClickListener { _ ->

            if (!validateAllDetails()) {
                return@setOnClickListener
            }

            val organisedStudyScheduleRequest = OrganisedStudyScheduleRequest(
                courseId.toString(),
                subjectId.toString(),
                getLessonIdList()!!,
                languageId.toString(),
                weightage,
                binding.startDate.text.toString(),
                binding.endDate.text.toString(),
                binding.holidayDates.text.toString(),
                availableScheduleId.toString(),
                getCheckedDays(),
                totalStudyTime.toString(),

                )
            Log.d(TAG, organisedStudyScheduleRequest.toString())
            studyOrganiserViewModel.scheduleOrganisedStudy(
                organisedStudyScheduleRequest
            )
        }
    }

    private fun getLessonIdList(): List<String>? {
        val lessonsString =
            binding.lessonMultiAutoCompleteTextView.text?.toString()
        val lessonNameList =
            lessonsString?.split(", ")?.filter { item -> item.isNotBlank() }
        Log.d(TAG, lessonNameList.toString())
        val lessonIdList = lessonNameList?.map { item -> "${lessonMap[item]}" }
        Log.d(TAG, lessonIdList.toString())
        return lessonIdList
    }

    private fun getLessonNamesListFromIds(lessonIdString: String): List<String> {
        val lessonIdList =
            lessonIdString.split(",").filter { item -> item.isNotBlank() }
        return lessonIdList.mapNotNull { id -> lessonMap.entries.firstOrNull { "${it.value}" == id }?.key }
    }

    private fun getCourseNameFromId(courseID: Int): String? {
        return courseMap.entries.firstOrNull { it.value == courseID }?.key
    }

    private fun getWeightageNameFromId(weightageID: String): String? {
        return weightageMap.entries.firstOrNull { it.value == weightageID }?.key
    }

    private fun getSubjectNameFromId(subjectID: Int): String? {
        return subjectMap.entries.firstOrNull { it.value == subjectID }?.key
    }

    private fun getLanguageFromId(languageID: Int): String? {
        return languageMap.entries.firstOrNull { it.value == languageID }?.key
    }

    private fun setCheckedDays(selectedDaysString: String) {
        val selectedDaysList =
            selectedDaysString.split(",").filter { item -> item.isNotBlank() }

        if (selectedDaysList.size == 7) {
            binding.allCheck.checkedState = STATE_CHECKED
        }

        selectedDaysList.forEach {
            when (it) {
                "S" -> binding.sundayCheck.checkedState = STATE_CHECKED
                "M" -> binding.mondayCheck.checkedState = STATE_CHECKED
                "T" -> binding.tuesdayCheck.checkedState = STATE_CHECKED
                "W" -> binding.wednesdayCheck.checkedState = STATE_CHECKED
                "H" -> binding.thursdayCheck.checkedState = STATE_CHECKED
                "F" -> binding.fridayCheck.checkedState = STATE_CHECKED
                "R" -> binding.saturdayCheck.checkedState = STATE_CHECKED
            }
            binding.allCheck.isEnabled = true
            binding.sundayCheck.isEnabled = true
            binding.mondayCheck.isEnabled = true
            binding.tuesdayCheck.isEnabled = true
            binding.wednesdayCheck.isEnabled = true
            binding.thursdayCheck.isEnabled = true
            binding.fridayCheck.isEnabled = true
            binding.saturdayCheck.isEnabled = true
        }

    }

    private fun validateAllDetails(): Boolean {
        if (courseId == 0) {
            Toast.makeText(requireContext(), "Please Select Course", Toast.LENGTH_SHORT).show()
            return false
        } else if (subjectId == 0) {
            Toast.makeText(requireContext(), "Please Select Subject", Toast.LENGTH_SHORT).show()
            return false
        } else if (getLessonIdList()?.size == 0) {
            Toast.makeText(requireContext(), "Please Select Lesson", Toast.LENGTH_SHORT).show()
            return false
        } else if (languageId == 0) {
            Toast.makeText(requireContext(), "Please Select Language", Toast.LENGTH_SHORT).show()
            return false
        } else if (weightage == "") {
            Toast.makeText(requireContext(), "Please Select Weightage", Toast.LENGTH_SHORT).show()
            return false
        } else if (binding.startDate.text.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Please Select Start Date", Toast.LENGTH_SHORT).show()
            return false
        } else if (binding.endDate.text.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Please Select End Date", Toast.LENGTH_SHORT).show()
            return false
        } else if (totalStudyTime == 0L) {
            Toast.makeText(requireContext(), "Total Study Time is Zero(0)", Toast.LENGTH_SHORT)
                .show()
            return false
        } else if (getCheckedDays().size == 0) {
            Toast.makeText(requireContext(), "Please Select Days", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun getCheckedDays(): ArrayList<String> {
        val daysList = ArrayList<String>()
        if (binding.allCheck.checkedState == STATE_CHECKED) {
            return arrayListOf(
                "Sunday",
                "Monday",
                "Tuesday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday"
            )
        }
        if (binding.sundayCheck.checkedState == STATE_CHECKED) {
            daysList.add("Sunday")
        }
        if (binding.mondayCheck.checkedState == STATE_CHECKED) {
            daysList.add("Monday")
        }
        if (binding.tuesdayCheck.checkedState == STATE_CHECKED) {
            daysList.add("Tuesday")
        }
        if (binding.wednesdayCheck.checkedState == STATE_CHECKED) {
            daysList.add("Wednesday")
        }
        if (binding.thursdayCheck.checkedState == STATE_CHECKED) {
            daysList.add("Thursday")
        }
        if (binding.fridayCheck.checkedState == STATE_CHECKED) {
            daysList.add("Friday")
        }
        if (binding.saturdayCheck.checkedState == STATE_CHECKED) {
            daysList.add("Saturday")
        }
        return daysList
    }

    private fun handleRadioButtonCheck() {

        binding.allCheck.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.sundayCheck.checkedState = STATE_CHECKED
                binding.mondayCheck.checkedState = STATE_CHECKED
                binding.tuesdayCheck.checkedState = STATE_CHECKED
                binding.wednesdayCheck.checkedState = STATE_CHECKED
                binding.thursdayCheck.checkedState = STATE_CHECKED
                binding.fridayCheck.checkedState = STATE_CHECKED
                binding.saturdayCheck.checkedState = STATE_CHECKED

                val allNumOfDays =
                    numOfSunday + numOfMonday + numOfTuesday + numOfWednesday + numOfThursday + numOfFriday + numOfSaturday
                val totalSelectedTime = if (allNumOfDays != 0L) {
                    totalStudyTime / (allNumOfDays)
                } else {
                    Toast.makeText(requireContext(), "There is no day to Study", Toast.LENGTH_SHORT)
                        .show()
                    totalStudyTime
                }

                val hours = TimeUnit.MINUTES.toHours(totalSelectedTime)
                val minutes = totalSelectedTime - TimeUnit.HOURS.toMinutes(hours)
                binding.hour.setText(hours.toString())
                binding.minutes.setText("${minutes.toInt()}")

            } else {
                binding.sundayCheck.checkedState = STATE_UNCHECKED
                binding.mondayCheck.checkedState = STATE_UNCHECKED
                binding.tuesdayCheck.checkedState = STATE_UNCHECKED
                binding.wednesdayCheck.checkedState = STATE_UNCHECKED
                binding.thursdayCheck.checkedState = STATE_UNCHECKED
                binding.fridayCheck.checkedState = STATE_UNCHECKED
                binding.saturdayCheck.checkedState = STATE_UNCHECKED

                val hours = TimeUnit.MINUTES.toHours(totalStudyTime)
                val minutes = totalStudyTime - TimeUnit.HOURS.toMinutes(hours)

                binding.hour.setText(hours.toString())
                binding.minutes.setText("${minutes.toInt()}")
            }
        }

        binding.sundayCheck.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                var selectedNoOfDays = numOfSunday
                if (binding.mondayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfMonday
                }
                if (binding.tuesdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfTuesday
                }
                if (binding.wednesdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfWednesday
                }
                if (binding.thursdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfThursday
                }
                if (binding.fridayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfFriday
                }
                if (binding.saturdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfSaturday
                }
                val totalSelectedTime = if (selectedNoOfDays != 0L) {
                    totalStudyTime / (selectedNoOfDays)
                } else {
                    totalStudyTime
                }
                val hours = TimeUnit.MINUTES.toHours(totalSelectedTime)
                val minutes = totalSelectedTime - TimeUnit.HOURS.toMinutes(hours)

                binding.hour.setText(hours.toString())
                binding.minutes.setText("${minutes.toInt()}")
            } else {
                var selectedNoOfDays = 0L
                if (binding.mondayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfMonday
                }
                if (binding.tuesdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfTuesday
                }
                if (binding.wednesdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfWednesday
                }
                if (binding.thursdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfThursday
                }
                if (binding.fridayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfFriday
                }
                if (binding.saturdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfSaturday
                }
                val totalSelectedTime = if (selectedNoOfDays != 0L) {
                    totalStudyTime / (selectedNoOfDays)
                } else {
                    totalStudyTime
                }
                val hours = TimeUnit.MINUTES.toHours(totalSelectedTime)
                val minutes = totalSelectedTime - TimeUnit.HOURS.toMinutes(hours)

                binding.hour.setText(hours.toString())
                binding.minutes.setText("${minutes.toInt()}")

            }
        }

        binding.mondayCheck.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                var selectedNoOfDays = numOfMonday
                if (binding.sundayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfSunday
                }
                if (binding.tuesdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfTuesday
                }
                if (binding.wednesdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfWednesday
                }
                if (binding.thursdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfThursday
                }
                if (binding.fridayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfFriday
                }
                if (binding.saturdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfSaturday
                }
                val totalSelectedTime = if (selectedNoOfDays != 0L) {
                    totalStudyTime / (selectedNoOfDays)
                } else {
                    totalStudyTime
                }
                val hours = TimeUnit.MINUTES.toHours(totalSelectedTime)
                val minutes = totalSelectedTime - TimeUnit.HOURS.toMinutes(hours)

                binding.hour.setText(hours.toString())
                binding.minutes.setText("${minutes.toInt()}")
            } else {
                var selectedNoOfDays = 0L
                if (binding.sundayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfSunday
                }
                if (binding.tuesdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfTuesday
                }
                if (binding.wednesdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfWednesday
                }
                if (binding.thursdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfThursday
                }
                if (binding.fridayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfFriday
                }
                if (binding.saturdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfSaturday
                }
                val totalSelectedTime = if (selectedNoOfDays != 0L) {
                    totalStudyTime / (selectedNoOfDays)
                } else {
                    totalStudyTime
                }
                val hours = TimeUnit.MINUTES.toHours(totalSelectedTime)
                val minutes = totalSelectedTime - TimeUnit.HOURS.toMinutes(hours)

                binding.hour.setText(hours.toString())
                binding.minutes.setText("${minutes.toInt()}")

            }
        }

        binding.tuesdayCheck.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                var selectedNoOfDays = numOfTuesday
                if (binding.sundayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfSunday
                }
                if (binding.mondayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfMonday
                }
                if (binding.wednesdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfWednesday
                }
                if (binding.thursdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfThursday
                }
                if (binding.fridayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfFriday
                }
                if (binding.saturdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfSaturday
                }
                val totalSelectedTime = if (selectedNoOfDays != 0L) {
                    totalStudyTime / (selectedNoOfDays)
                } else {
                    totalStudyTime
                }
                val hours = TimeUnit.MINUTES.toHours(totalSelectedTime)
                val minutes = totalSelectedTime - TimeUnit.HOURS.toMinutes(hours)

                binding.hour.setText(hours.toString())
                binding.minutes.setText("${minutes.toInt()}")
            } else {
                var selectedNoOfDays = 0L
                if (binding.sundayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfSunday
                }
                if (binding.mondayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfMonday
                }
                if (binding.wednesdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfWednesday
                }
                if (binding.thursdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfThursday
                }
                if (binding.fridayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfFriday
                }
                if (binding.saturdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfSaturday
                }
                val totalSelectedTime = if (selectedNoOfDays != 0L) {
                    totalStudyTime / (selectedNoOfDays)
                } else {
                    totalStudyTime
                }
                val hours = TimeUnit.MINUTES.toHours(totalSelectedTime)
                val minutes = totalSelectedTime - TimeUnit.HOURS.toMinutes(hours)

                binding.hour.setText(hours.toString())
                binding.minutes.setText("${minutes.toInt()}")

            }
        }

        binding.wednesdayCheck.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                var selectedNoOfDays = numOfWednesday
                if (binding.sundayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfSunday
                }
                if (binding.mondayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfMonday
                }
                if (binding.tuesdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfTuesday
                }
                if (binding.thursdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfThursday
                }
                if (binding.fridayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfFriday
                }
                if (binding.saturdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfSaturday
                }
                val totalSelectedTime = if (selectedNoOfDays != 0L) {
                    totalStudyTime / (selectedNoOfDays)
                } else {
                    totalStudyTime
                }
                val hours = TimeUnit.MINUTES.toHours(totalSelectedTime)
                val minutes = totalSelectedTime - TimeUnit.HOURS.toMinutes(hours)

                binding.hour.setText(hours.toString())
                binding.minutes.setText("${minutes.toInt()}")
            } else {
                var selectedNoOfDays = 0L
                if (binding.sundayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfSunday
                }
                if (binding.mondayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfMonday
                }
                if (binding.tuesdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfTuesday
                }
                if (binding.thursdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfThursday
                }
                if (binding.fridayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfFriday
                }
                if (binding.saturdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfSaturday
                }
                val totalSelectedTime = if (selectedNoOfDays != 0L) {
                    totalStudyTime / (selectedNoOfDays)
                } else {
                    totalStudyTime
                }
                val hours = TimeUnit.MINUTES.toHours(totalSelectedTime)
                val minutes = totalSelectedTime - TimeUnit.HOURS.toMinutes(hours)

                binding.hour.setText(hours.toString())
                binding.minutes.setText("${minutes.toInt()}")

            }
        }

        binding.thursdayCheck.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                var selectedNoOfDays = numOfThursday
                if (binding.sundayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfSunday
                }
                if (binding.mondayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfMonday
                }
                if (binding.tuesdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfTuesday
                }
                if (binding.wednesdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfWednesday
                }
                if (binding.fridayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfFriday
                }
                if (binding.saturdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfSaturday
                }
                val totalSelectedTime = if (selectedNoOfDays != 0L) {
                    totalStudyTime / (selectedNoOfDays)
                } else {
                    totalStudyTime
                }
                val hours = TimeUnit.MINUTES.toHours(totalSelectedTime)
                val minutes = totalSelectedTime - TimeUnit.HOURS.toMinutes(hours)

                binding.hour.setText(hours.toString())
                binding.minutes.setText("${minutes.toInt()}")
            } else {
                var selectedNoOfDays = 0L
                if (binding.sundayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfSunday
                }
                if (binding.mondayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfMonday
                }
                if (binding.tuesdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfTuesday
                }
                if (binding.wednesdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfWednesday
                }
                if (binding.fridayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfFriday
                }
                if (binding.saturdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfSaturday
                }
                val totalSelectedTime = if (selectedNoOfDays != 0L) {
                    totalStudyTime / (selectedNoOfDays)
                } else {
                    totalStudyTime
                }
                val hours = TimeUnit.MINUTES.toHours(totalSelectedTime)
                val minutes = totalSelectedTime - TimeUnit.HOURS.toMinutes(hours)

                binding.hour.setText(hours.toString())
                binding.minutes.setText("${minutes.toInt()}")

            }
        }

        binding.fridayCheck.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                var selectedNoOfDays = numOfFriday
                if (binding.sundayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfSunday
                }
                if (binding.mondayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfMonday
                }
                if (binding.tuesdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfTuesday
                }
                if (binding.wednesdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfWednesday
                }
                if (binding.thursdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfThursday
                }
                if (binding.saturdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfSaturday
                }
                val totalSelectedTime = if (selectedNoOfDays != 0L) {
                    totalStudyTime / (selectedNoOfDays)
                } else {
                    totalStudyTime
                }
                val hours = TimeUnit.MINUTES.toHours(totalSelectedTime)
                val minutes = totalSelectedTime - TimeUnit.HOURS.toMinutes(hours)

                binding.hour.setText(hours.toString())
                binding.minutes.setText("${minutes.toInt()}")
            } else {
                var selectedNoOfDays = 0L
                if (binding.sundayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfSunday
                }
                if (binding.mondayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfMonday
                }
                if (binding.tuesdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfTuesday
                }
                if (binding.wednesdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfWednesday
                }
                if (binding.thursdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfThursday
                }
                if (binding.saturdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfSaturday
                }
                val totalSelectedTime = if (selectedNoOfDays != 0L) {
                    totalStudyTime / (selectedNoOfDays)
                } else {
                    totalStudyTime
                }
                val hours = TimeUnit.MINUTES.toHours(totalSelectedTime)
                val minutes = totalSelectedTime - TimeUnit.HOURS.toMinutes(hours)

                binding.hour.setText(hours.toString())
                binding.minutes.setText("${minutes.toInt()}")

            }
        }

        binding.saturdayCheck.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                var selectedNoOfDays = numOfSaturday
                if (binding.sundayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfSunday
                }
                if (binding.mondayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfMonday
                }
                if (binding.tuesdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfTuesday
                }
                if (binding.wednesdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfWednesday
                }
                if (binding.thursdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfThursday
                }
                if (binding.fridayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfFriday
                }
                val totalSelectedTime = if (selectedNoOfDays != 0L) {
                    totalStudyTime / (selectedNoOfDays)
                } else {
                    totalStudyTime
                }
                val hours = TimeUnit.MINUTES.toHours(totalSelectedTime)
                val minutes = totalSelectedTime - TimeUnit.HOURS.toMinutes(hours)

                binding.hour.setText(hours.toString())
                binding.minutes.setText("${minutes.toInt()}")
            } else {
                var selectedNoOfDays = 0L
                if (binding.sundayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfSunday
                }
                if (binding.mondayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfMonday
                }
                if (binding.tuesdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfTuesday
                }
                if (binding.wednesdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfWednesday
                }
                if (binding.thursdayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfThursday
                }
                if (binding.fridayCheck.checkedState == STATE_CHECKED) {
                    selectedNoOfDays += numOfFriday
                }
                val totalSelectedTime = if (selectedNoOfDays != 0L) {
                    totalStudyTime / (selectedNoOfDays)
                } else {
                    totalStudyTime
                }
                val hours = TimeUnit.MINUTES.toHours(totalSelectedTime)
                val minutes = totalSelectedTime - TimeUnit.HOURS.toMinutes(hours)

                binding.hour.setText(hours.toString())
                binding.minutes.setText("${minutes.toInt()}")

            }
        }

    }

    private fun getOrganiserUtils() {

        studyOrganiserViewModel.getOrganiserCourses(GetOrganiserUtilRequest("courses"))
        studyOrganiserViewModel.getOrganiserLanguages(GetOrganiserUtilRequest("language"))
        studyOrganiserViewModel.getOrganiserWeightages(GetOrganiserUtilRequest("weightage"))

    }

    private fun getOrganiserStudyTime(getOrganiserStudyTimeRequest: GetOrganiserStudyTimeRequest) {
        studyOrganiserViewModel.getOrganiserStudyTime(getOrganiserStudyTimeRequest)
    }

    private fun calculateDaysBetween(startDay: Date, endDay: Date, dayOfWeek: Int): Long {
        val startCalendar = Calendar.getInstance().apply { time = startDay }
        val endCalendar = Calendar.getInstance().apply { time = endDay }

        val currentDate = startCalendar.clone() as Calendar
        // Log.d(TAG, currentDate.time.toString())
        var daysCount = 0L

        while (!currentDate.after(endCalendar)) {
            if (currentDate.get(Calendar.DAY_OF_WEEK) == dayOfWeek) {
                currentDate.add(Calendar.DAY_OF_WEEK, 7)
                daysCount++
            } else {
                currentDate.add(Calendar.DAY_OF_WEEK, 1)
            }
            //  Log.d(TAG, currentDate.time.toString())
        }
        Log.d(TAG, "$dayOfWeek count is $daysCount")
        return daysCount
    }

    private fun doubleSelectToRemoveLesson(selectedItem: Any?) {

        val lessonsString = binding.lessonMultiAutoCompleteTextView.text?.toString()
        val lessonNameList =
            lessonsString?.split(", ")?.filter { item -> item.isNotBlank() }?.toMutableList()
        val occurrences = lessonNameList?.count { item -> item == selectedItem }
        Log.d(TAG, "$occurrences")
        if ((occurrences ?: 0) == 2) {
            lessonNameList?.removeLast()
            lessonNameList?.remove(selectedItem)
            binding.lessonMultiAutoCompleteTextView.text = null
            if (lessonNameList?.size!! > 0) {
                binding.lessonMultiAutoCompleteTextView.append(
                    lessonNameList.joinToString(", ").plus(", ")
                )
            }
            Toast.makeText(
                requireContext(),
                "$selectedItem is removed",
                Toast.LENGTH_SHORT
            ).show()
        }

        weightage = ""
        binding.weightageAutoCompleteTextView.text = null
        binding.hour.text = null
        binding.minutes.text = null
        totalStudyTime = 0L

        binding.allCheck.checkedState = STATE_UNCHECKED
        binding.sundayCheck.checkedState = STATE_UNCHECKED
        binding.mondayCheck.checkedState = STATE_UNCHECKED
        binding.tuesdayCheck.checkedState = STATE_UNCHECKED
        binding.wednesdayCheck.checkedState = STATE_UNCHECKED
        binding.thursdayCheck.checkedState = STATE_UNCHECKED
        binding.fridayCheck.checkedState = STATE_UNCHECKED
        binding.saturdayCheck.checkedState = STATE_UNCHECKED

    }

    private fun getOrganiserLessons(getOrganiserLessonsRequest: GetOrganiserLessonsRequest) {
        studyOrganiserViewModel.getOrganiserLessons(getOrganiserLessonsRequest)

    }

    private fun getOrganiserSubjects(courseId: Int) {
        studyOrganiserViewModel.getOrganiserSubjects(
            GetOrganiserSubjectsRequest(
                courseId.toString(),
                "subjects"
            )
        )
    }

    private fun setHolidayDates() {
        binding.holidayDates.setOnClickListener {
            if (binding.startDate.text.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "First fill start date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (binding.endDate.text.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "First fill end date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            showDatePicker(binding.holidayDates)
        }

    }

    private fun showDatePicker(textInputEditText: TextInputEditText) {

        val builder = MaterialDatePicker.Builder.datePicker()

        val picker = builder.build()

        picker.addOnPositiveButtonClickListener {

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val selectedDate = dateFormat.format(Date(it))
            if (binding.startDate == textInputEditText) {
                textInputEditText.setText(selectedDate)

                binding.allCheck.checkedState = STATE_UNCHECKED
                binding.sundayCheck.checkedState = STATE_UNCHECKED
                binding.mondayCheck.checkedState = STATE_UNCHECKED
                binding.tuesdayCheck.checkedState = STATE_UNCHECKED
                binding.wednesdayCheck.checkedState = STATE_UNCHECKED
                binding.thursdayCheck.checkedState = STATE_UNCHECKED
                binding.fridayCheck.checkedState = STATE_UNCHECKED
                binding.saturdayCheck.checkedState = STATE_UNCHECKED

                val hours = TimeUnit.MINUTES.toHours(totalStudyTime)
                val minutes = totalStudyTime - TimeUnit.HOURS.toMinutes(hours)

                binding.hour.setText(hours.toString())
                binding.minutes.setText("${minutes.toInt()}")


                if (!binding.endDate.text.isNullOrEmpty()) {
                    val endDate = dateFormat.parse(binding.endDate.text.toString())
                    val selectedDateTime = dateFormat.parse(selectedDate)?.time!!
                    if (selectedDateTime > endDate!!.time) {
                        val sweetAlertDialog =
                            SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                        sweetAlertDialog.titleText = "Start date is greater than End date !"
                        sweetAlertDialog.confirmText = "OK"
                        sweetAlertDialog.setOnDismissListener {
                            binding.startDate.text = null
                        }
                        sweetAlertDialog.show()
                    }
                }

            } else if (binding.endDate == textInputEditText) {
                if (!binding.startDate.text.isNullOrEmpty()) {
                    val startDate = dateFormat.parse(binding.startDate.text.toString())
                    val selectedDateTime = dateFormat.parse(selectedDate)?.time!!
                    if (selectedDateTime < startDate!!.time) {
                        val sweetAlertDialog =
                            SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                        sweetAlertDialog.titleText = "End date is smaller than Start date !"
                        sweetAlertDialog.confirmText = "OK"
                        sweetAlertDialog.setOnDismissListener {
                            binding.endDate.text = null
                        }
                        sweetAlertDialog.show()
                    } else {
                        textInputEditText.setText(selectedDate)
                    }
                }
                if (!binding.startDate.text.isNullOrEmpty() && !binding.endDate.text.isNullOrEmpty()) {
                    val startDate = dateFormat.parse(binding.startDate.text.toString())!!
                    val endDate = dateFormat.parse(binding.endDate.text.toString())!!
                    Log.d(TAG, "endDate $endDate")
                    numOfSunday = calculateDaysBetween(startDate, endDate, Calendar.SUNDAY)
                    numOfMonday = calculateDaysBetween(startDate, endDate, Calendar.MONDAY)
                    numOfTuesday = calculateDaysBetween(startDate, endDate, Calendar.TUESDAY)
                    numOfWednesday = calculateDaysBetween(startDate, endDate, Calendar.WEDNESDAY)
                    numOfThursday = calculateDaysBetween(startDate, endDate, Calendar.THURSDAY)
                    numOfFriday = calculateDaysBetween(startDate, endDate, Calendar.FRIDAY)
                    numOfSaturday = calculateDaysBetween(startDate, endDate, Calendar.SATURDAY)

                    binding.allCheck.isEnabled = true
                    binding.sundayCheck.isEnabled = true
                    binding.mondayCheck.isEnabled = true
                    binding.tuesdayCheck.isEnabled = true
                    binding.wednesdayCheck.isEnabled = true
                    binding.thursdayCheck.isEnabled = true
                    binding.fridayCheck.isEnabled = true
                    binding.saturdayCheck.isEnabled = true


                    binding.allCheck.checkedState = STATE_UNCHECKED
                    binding.sundayCheck.checkedState = STATE_UNCHECKED
                    binding.mondayCheck.checkedState = STATE_UNCHECKED
                    binding.tuesdayCheck.checkedState = STATE_UNCHECKED
                    binding.wednesdayCheck.checkedState = STATE_UNCHECKED
                    binding.thursdayCheck.checkedState = STATE_UNCHECKED
                    binding.fridayCheck.checkedState = STATE_UNCHECKED
                    binding.saturdayCheck.checkedState = STATE_UNCHECKED

                    val hours = TimeUnit.MINUTES.toHours(totalStudyTime)
                    val minutes = totalStudyTime - TimeUnit.HOURS.toMinutes(hours)

                    binding.hour.setText(hours.toString())
                    binding.minutes.setText("${minutes.toInt()}")

                }
            } else {
                if (!binding.endDate.text.isNullOrEmpty() && !binding.startDate.text.isNullOrEmpty()) {
                    val startDate = dateFormat.parse(binding.startDate.text.toString())!!
                    val endDate = dateFormat.parse(binding.endDate.text.toString())!!
                    val selectedDateTime = dateFormat.parse(selectedDate)?.time!!
                    Log.d(TAG, "$selectedDateTime, ${endDate.time}, ${startDate.time} ")
                    if (selectedDateTime > endDate.time || selectedDateTime < startDate.time) {
                        val sweetAlertDialog =
                            SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                        sweetAlertDialog.titleText =
                            "Holiday should be lie b/w ${binding.startDate.text} and ${binding.endDate.text} !"
                        sweetAlertDialog.confirmText = "OK"
                        sweetAlertDialog.show()
                    } else {
                        binding.allCheck.checkedState = STATE_UNCHECKED
                        binding.sundayCheck.checkedState = STATE_UNCHECKED
                        binding.mondayCheck.checkedState = STATE_UNCHECKED
                        binding.tuesdayCheck.checkedState = STATE_UNCHECKED
                        binding.wednesdayCheck.checkedState = STATE_UNCHECKED
                        binding.thursdayCheck.checkedState = STATE_UNCHECKED
                        binding.fridayCheck.checkedState = STATE_UNCHECKED
                        binding.saturdayCheck.checkedState = STATE_UNCHECKED

                        val hours = TimeUnit.MINUTES.toHours(totalStudyTime)
                        val minutes = totalStudyTime - TimeUnit.HOURS.toMinutes(hours)

                        binding.hour.setText(hours.toString())
                        binding.minutes.setText("${minutes.toInt()}")

                        if (holidayDateSet.add(selectedDate)) {
                            val holidayDateText = TextUtils.join(", ", holidayDateSet)
                            textInputEditText.setText(holidayDateText)

                        } else {
                            holidayDateSet.remove(selectedDate)
                            val holidayDateText = TextUtils.join(", ", holidayDateSet)
                            textInputEditText.setText(holidayDateText)
                            Toast.makeText(
                                requireContext(),
                                "$selectedDate is removed !",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        numOfSunday = calculateDaysBetween(startDate, endDate, Calendar.SUNDAY)
                        numOfMonday = calculateDaysBetween(startDate, endDate, Calendar.MONDAY)
                        numOfTuesday = calculateDaysBetween(startDate, endDate, Calendar.TUESDAY)
                        numOfWednesday =
                            calculateDaysBetween(startDate, endDate, Calendar.WEDNESDAY)
                        numOfThursday = calculateDaysBetween(startDate, endDate, Calendar.THURSDAY)
                        numOfFriday = calculateDaysBetween(startDate, endDate, Calendar.FRIDAY)
                        numOfSaturday = calculateDaysBetween(startDate, endDate, Calendar.SATURDAY)



                        for (i in holidayDateSet) {
                            val holidayDate = dateFormat.parse(i)
                            val holidayDateCalendar =
                                Calendar.getInstance().apply { time = holidayDate!! }
                            when {
                                holidayDateCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY -> {
                                    numOfSunday -= 1L
                                    Log.d(TAG, "sunday is left $numOfSunday")
                                }

                                holidayDateCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY -> {
                                    numOfMonday -= 1L
                                    Log.d(TAG, "monday is left $numOfMonday")
                                }

                                holidayDateCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY -> {
                                    numOfTuesday -= 1L
                                    Log.d(TAG, "tuesday is left $numOfTuesday")
                                }

                                holidayDateCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY -> {
                                    numOfWednesday -= 1L
                                    Log.d(TAG, "Wednesday is left $numOfWednesday")
                                }

                                holidayDateCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY -> {
                                    numOfThursday -= 1L
                                    Log.d(TAG, "thursday is left $numOfThursday")
                                }

                                holidayDateCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY -> {
                                    numOfFriday -= 1L
                                    Log.d(TAG, "friday is left $numOfFriday")
                                }

                                holidayDateCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY -> {
                                    numOfSaturday -= 1L
                                    Log.d(TAG, "saturday is left $numOfSaturday")
                                }
                            }
                        }
                    }
                }

            }

        }

        picker.show(parentFragmentManager, picker.toString())
    }

    private fun setStartAndEndDate() {
        binding.startDate.setOnClickListener {
            showDatePicker(binding.startDate)
        }
        binding.endDate.setOnClickListener {
            if (binding.startDate.text.isNullOrEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Please Select Start Date First",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            showDatePicker(binding.endDate)
        }

    }

    override fun onScheduleEditSelected(scheduleID: Int) {
        studyOrganiserViewModel.editOrganisedStudySchedules(scheduleID)
    }

}