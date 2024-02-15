package com.margdarshakendra.margdarshak.progress_meter_tab_fragments

import android.animation.LayoutTransition
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.view.children
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.textview.MaterialTextView
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.Week
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthScrollListener
import com.kizitonwose.calendar.view.ViewContainer
import com.kizitonwose.calendar.view.WeekDayBinder
import com.kizitonwose.calendar.view.WeekScrollListener
import com.margdarshakendra.margdarshak.R
import com.margdarshakendra.margdarshak.databinding.CalendarCellLayoutBinding
import com.margdarshakendra.margdarshak.databinding.FragmentCalenderBinding
import com.margdarshakendra.margdarshak.models.CalenderSchedulesResponse
import com.margdarshakendra.margdarshak.models.GetOrganiserUtilRequest
import com.margdarshakendra.margdarshak.utils.Constants.TAG
import com.margdarshakendra.margdarshak.utils.NetworkResult
import com.margdarshakendra.margdarshak.viewmodels.CalenderViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale


@AndroidEntryPoint
class CalenderFragment : Fragment() {

    private lateinit var binding: FragmentCalenderBinding
    private lateinit var studyDaysList: List<String>

    private val calenderViewModel by viewModels<CalenderViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalenderBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        studyDaysList = mutableListOf()

        setDaysTitles()

        calenderViewModel.getCalenderSchedules(GetOrganiserUtilRequest("calender"))


       /* binding.swipeRefresh.setOnRefreshListener {
            calenderViewModel.getCalenderSchedules(GetOrganiserUtilRequest("calender"))
        }*/


        calenderViewModel.calenderSchedulesLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.toString())
                  //  binding.swipeRefresh.isRefreshing = false
                    /* val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

                     //  binding.calenderView.setCalendarDayLayout(R.layout.calender_cell)
                     val calendarDays = mutableListOf<CalendarDay>()

                     for (lesson in it.data.lessons) {

                         //val lessonText = requireContext().getDrawableText(lesson.subject, Typeface.DEFAULT, R.color.upperBlue, 12)
                         // Log.d(TAG, lessonText.toString())
                         val startDate = Calendar.getInstance()
                         startDate.time = dateFormat.parse(lesson.date_start)!!

                         val endDate = Calendar.getInstance()
                         endDate.time = dateFormat.parse(lesson.date_finish)!!

                         val currentDate = startDate.clone() as Calendar
                         Log.d(TAG, currentDate.time.toString())
                         while (!currentDate.after(endDate)) {

                             val calendarDay = CalendarDay(currentDate)
                             calendarDay.labelColor = R.color.white
                             calendarDay.backgroundResource = R.drawable.circle
                             calendarDays.add(calendarDay)

                             // Move to the next date
                             currentDate.add(Calendar.DAY_OF_MONTH, 1)
                             Log.d(TAG, currentDate.time.toString())

                         }

                     }

                     binding.calenderView.setCalendarDays(calendarDays)*/

                    if(it.data.lessons.isNotEmpty()){
                        populateSubjectWiseCalender(it.data.lessons[0])
                        binding.subject.text = it.data.lessons[0].subject
                    }
                    else{
                        binding.mainLayout1.visibility = View.GONE
                        binding.mainLayout2.visibility = View.GONE
                        binding.mainLayout3.visibility = View.GONE
                        binding.mainLayout4.visibility = View.GONE
                        binding.todayText.text = "No schedules created yet !"
                    }

                    binding.moreOptionsMenu.setOnClickListener { view ->
                        showPopMenu(it.data.lessons, view)
                    }

                    /*for (lesson in it.data.lessons) {
                        studyDaysList = getStudyDaysList(lesson.study_days)
                        val startDate =
                            LocalDate.parse(lesson.date_start) // Replace with your start date
                        val endDate =
                            LocalDate.parse(lesson.date_finish) // Replace with your end date

                        // Iterate over the range of dates
                        var currentDate = startDate
                        currentDate = currentDate.plus(1, ChronoUnit.DAYS)
                        Log.d(TAG, currentDate.toString())
                        while (!currentDate.isAfter(endDate)) {

                            val pattern = "yyyy-MM-dd"

                            // Create a DateTimeFormatter using the pattern
                            val formatter = DateTimeFormatter.ofPattern(pattern)

                            // Format the LocalDate
                            val formattedCurrentDate = currentDate.format(formatter)

                            events.add(formattedCurrentDate)

                            binding.lessonText.text = lesson.subject

                            // Move to the next date
                            currentDate = currentDate.plus(1, ChronoUnit.DAYS)
                            Log.d(TAG, currentDate.toString())
                        }
                    }*/

                    /*val titlesContainer: ViewGroup? =
                        requireActivity().findViewById(R.id.titlesContainer)
                    titlesContainer?.children
                        ?.map { titleTextView-> titleTextView as MaterialTextView }
                        ?.forEachIndexed { index, materialTextView ->
                            val dayOfWeek = daysOfWeek()[index]
                            val title =
                                dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                            materialTextView.text = title
                        }

                    binding.chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
                        if (checkedIds.isNotEmpty()) {
                            when (checkedIds[0]) {
                                R.id.monthlyWise -> {
                                    binding.calendarView.visibility = View.VISIBLE
                                    binding.weekCalendarView.visibility = View.GONE
                                    showMonthCalender(events)
                                }

                                R.id.weeklyWise -> {
                                    binding.weekCalendarView.visibility = View.VISIBLE
                                    binding.calendarView.visibility = View.GONE
                                    showWeekCalender(events)
                                }
                            }
                        }

                    }


                    binding.monthlyWise.isChecked = true
*/

                    /*
                    binding.prevMonthBtn.setOnClickListener {
                        if (binding.calendarView.visibility == View.VISIBLE){
                            currentMonth = currentMonth.previousMonth
                            binding.calendarView.smoothScrollToMonth(currentMonth)
                        }
                        else{
                            currentDate = currentDate.minusWeeks(1)
                            binding.weekCalendarView.smoothScrollToDate(currentDate)
                        }

                    }

                    binding.nextMonthBtn.setOnClickListener {
                        if (binding.calendarView.visibility == View.VISIBLE){
                            currentMonth = currentMonth.nextMonth
                            binding.calendarView.smoothScrollToMonth(currentMonth)
                        }
                        else{
                            currentDate = currentDate.plusWeeks(1)
                            binding.weekCalendarView.smoothScrollToWeek(currentDate)
                        }
                    }
*/

                }

                is NetworkResult.Error -> {
                   // binding.swipeRefresh.isRefreshing = false
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {}
            }
        }
    }

    private fun showPopMenu(lessons: List<CalenderSchedulesResponse.Lesson>, view: View) {
        Log.d(TAG, "show pop menu is clicked")
        val popupMenu = PopupMenu(requireContext(), view)
        val lessonMap = LinkedHashMap<Int, CalenderSchedulesResponse.Lesson>()

        for ((menuId, lesson) in lessons.withIndex()) {
            lessonMap[menuId] = lesson
            popupMenu.menu.add(Menu.NONE, menuId, Menu.NONE, lesson.subject)
            Log.d(TAG, popupMenu.menu.findItem(menuId).toString())
        }

        popupMenu.setOnMenuItemClickListener {menuItem ->
            when (menuItem.itemId) {
                in lessonMap.keys -> {
                    val selectedLesson = lessonMap[menuItem.itemId]

                    populateSubjectWiseCalender(selectedLesson!!)
                    binding.subject.text = selectedLesson.subject

                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }


    private fun setDaysTitles(){
        val titlesContainer: ViewGroup? =
            requireActivity().findViewById(R.id.titlesContainer)
        titlesContainer?.children
            ?.map { titleTextView-> titleTextView as MaterialTextView }
            ?.forEachIndexed { index, materialTextView ->
                val dayOfWeek = daysOfWeek()[index]
                val title =
                    dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                materialTextView.text = title
            }
    }

    private fun populateSubjectWiseCalender(lesson: CalenderSchedulesResponse.Lesson){
        studyDaysList = getStudyDaysList(lesson.study_days)
        val startDate =
            LocalDate.parse(lesson.date_start) // Replace with your start date
        val endDate =
            LocalDate.parse(lesson.date_finish) // Replace with your end date

        // Iterate over the range of dates
        var currentDate = startDate
        currentDate = currentDate.plus(1, ChronoUnit.DAYS)
        Log.d(TAG, currentDate.toString())
        val events = mutableListOf<String>()

        while (!currentDate.isAfter(endDate)) {

            val pattern = "yyyy-MM-dd"

            // Create a DateTimeFormatter using the pattern
            val formatter = DateTimeFormatter.ofPattern(pattern)

            // Format the LocalDate
            val formattedCurrentDate = currentDate.format(formatter)


            events.add(formattedCurrentDate)

            binding.lessonText.text = lesson.subject

            // Move to the next date
            currentDate = currentDate.plus(1, ChronoUnit.DAYS)
            Log.d(TAG, currentDate.toString())
        }

        setDaysTitles()

        binding.chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                when (checkedIds[0]) {
                    R.id.monthlyWise -> {
                        binding.calendarView.visibility = View.VISIBLE
                        binding.weekCalendarView.visibility = View.GONE
                        showMonthCalender(events)
                    }

                    R.id.weeklyWise -> {
                        binding.weekCalendarView.visibility = View.VISIBLE
                        binding.calendarView.visibility = View.GONE
                        showWeekCalender(events)
                    }
                }
            }

        }


        binding.monthlyWise.isChecked = true
        showMonthCalender(events)
    }


    private fun showMonthCalender(events: List<String>) {
        var currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100)  // Adjust as needed
        val endMonth = currentMonth.plusMonths(100)  // Adjust as needed
        val firstDayOfWeek = firstDayOfWeekFromLocale() // Available from the library
        binding.calendarView.setup(startMonth, endMonth, firstDayOfWeek)
        binding.calendarView.scrollToMonth(currentMonth)

        binding.calendarView.dayBinder = null

        binding.calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                val localDate = data.date

                val pattern = "yyyy-MM-dd"

                // Create a DateTimeFormatter using the pattern
                val formatter = DateTimeFormatter.ofPattern(pattern)

                // Format the LocalDate
                val formattedLocalDate = localDate.format(formatter)
                val today = LocalDate.now().format(formatter)

                container.textView.text = localDate.dayOfMonth.toString()
                val dayOfWeek =
                    localDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
                Log.d(TAG, dayOfWeek)

                if (data.position == DayPosition.MonthDate ) {
                    container.textView.setTextColor(Color.BLACK)
                    container.cardView.setCardBackgroundColor(
                        resources.getColor(
                            R.color.white,
                            null
                        )
                    )
                }
                if(data.position != DayPosition.MonthDate) {
                    container.cardView.setCardBackgroundColor(
                        resources.getColor(
                            R.color.white,
                            null
                        )
                    )
                    container.textView.setTextColor(Color.GRAY)
                }
                if (events.contains(formattedLocalDate) && studyDaysList.contains(dayOfWeek)) {
                    Log.d(TAG, "true+$formattedLocalDate")

                    binding.lessonText.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.circle,
                        0,
                        0,
                        0
                    )
                    TextViewCompat.setCompoundDrawableTintList(
                        binding.lessonText,
                        ColorStateList.valueOf(resources.getColor(R.color.upperBlue, null))
                    )

                    container.cardView.setCardBackgroundColor(
                        resources.getColor(
                            R.color.upperBlue,
                            null
                        )
                    )
                    container.textView.setTextColor(resources.getColor(R.color.white, null))
                }

                if (formattedLocalDate == today) {
                    Log.d(TAG, "true+$formattedLocalDate, $today")

                    binding.todayText.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.circle,
                        0,
                        0,
                        0
                    )
                    TextViewCompat.setCompoundDrawableTintList(
                        binding.todayText,
                        ColorStateList.valueOf(resources.getColor(R.color.parrotGreen, null))
                    )

                    container.textView.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        0,
                        R.drawable.circle
                    )
                    container.textView.setPadding(0, 0, 0, 10)

                    if (!events.contains(today)) {
                        container.cardView.setCardBackgroundColor(
                            resources.getColor(
                                R.color.parrotGreen,
                                null
                            )
                        )
                        container.textView.setTextColor(resources.getColor(R.color.white, null))
                        TextViewCompat.setCompoundDrawableTintList(
                            container.textView,
                            ColorStateList.valueOf(resources.getColor(R.color.white, null))
                        )
                    }
                    else if(events.contains(today) &&  ! studyDaysList.contains(dayOfWeek) ) {
                        container.cardView.setCardBackgroundColor(
                            resources.getColor(
                                R.color.parrotGreen,
                                null
                            )
                        )
                        container.textView.setTextColor(resources.getColor(R.color.white, null))
                        TextViewCompat.setCompoundDrawableTintList(
                            container.textView,
                            ColorStateList.valueOf(resources.getColor(R.color.white, null))
                        )
                    }
                    else {
                        container.textView.setTextColor(resources.getColor(R.color.white, null))
                        TextViewCompat.setCompoundDrawableTintList(
                            container.textView,
                            ColorStateList.valueOf(resources.getColor(R.color.parrotGreen, null))
                        )
                    }
                } else{
                    container.textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }

            }

            override fun create(view: View): DayViewContainer {
                // val ccBinding = CalendarCellLayoutBinding.inflate(layoutInflater)
                return DayViewContainer(view)
            }

        }

        binding.calendarView.monthScrollListener = object : MonthScrollListener {
            override fun invoke(p1: CalendarMonth) {
                Log.d(TAG, "${p1.yearMonth.month}, ${p1.yearMonth.year}")
                val currentYearMonth = "${p1.yearMonth.month} ${p1.yearMonth.year}"
                currentMonth = p1.yearMonth
                binding.yearMonthText.text = currentYearMonth
            }
        }

        binding.prevMonthBtn.setOnClickListener {
            if (binding.calendarView.visibility == View.VISIBLE) {
                currentMonth = currentMonth.previousMonth
                Log.d(TAG, currentMonth.toString())
                binding.calendarView.smoothScrollToMonth(currentMonth)
            }
        }

        binding.nextMonthBtn.setOnClickListener {
            if (binding.calendarView.visibility == View.VISIBLE) {
                currentMonth = currentMonth.nextMonth
                Log.d(TAG, currentMonth.toString())
                binding.calendarView.smoothScrollToMonth(currentMonth)
            }
        }
    }

    private fun showWeekCalender(events: List<String>) {
        var currentDate = LocalDate.now()
        val currentMonthForWeek = YearMonth.now()
        val startDate = currentMonthForWeek.minusMonths(100).atStartOfMonth() // Adjust as needed
        val endDate = currentMonthForWeek.plusMonths(100).atEndOfMonth()  // Adjust as needed
        binding.weekCalendarView.setup(startDate, endDate, firstDayOfWeekFromLocale())
        binding.weekCalendarView.scrollToWeek(currentDate)

        binding.weekCalendarView.dayBinder = null

        binding.weekCalendarView.dayBinder = object : WeekDayBinder<DayViewContainer> {
            override fun bind(container: DayViewContainer, data: WeekDay) {
                val localDate = data.date

                val pattern = "yyyy-MM-dd"

                // Create a DateTimeFormatter using the pattern
                val formatter = DateTimeFormatter.ofPattern(pattern)

                // Format the LocalDate
                val formattedLocalDate = localDate.format(formatter)
                val today = LocalDate.now().format(formatter)

                container.textView.text = localDate.dayOfMonth.toString()
                val dayOfWeek =
                    localDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
                Log.d(TAG, dayOfWeek)

                if (events.contains(formattedLocalDate) && studyDaysList.contains(dayOfWeek)) {
                    Log.d(TAG, "true+$formattedLocalDate")

                    binding.lessonText.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.circle,
                        0,
                        0,
                        0
                    )
                    TextViewCompat.setCompoundDrawableTintList(
                        binding.lessonText,
                        ColorStateList.valueOf(resources.getColor(R.color.upperBlue, null))
                    )

                    container.cardView.setCardBackgroundColor(
                        resources.getColor(
                            R.color.upperBlue,
                            null
                        )
                    )
                    container.textView.setTextColor(resources.getColor(R.color.white, null))

                } else {
                    container.textView.setTextColor(Color.BLACK)
                    container.cardView.setCardBackgroundColor(
                        resources.getColor(
                            R.color.white,
                            null
                        )
                    )
                }

                if (formattedLocalDate == today) {
                    Log.d(TAG, "true+$formattedLocalDate, $today")

                    binding.todayText.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.circle,
                        0,
                        0,
                        0
                    )
                    TextViewCompat.setCompoundDrawableTintList(
                        binding.todayText,
                        ColorStateList.valueOf(resources.getColor(R.color.parrotGreen, null))
                    )

                    container.textView.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        0,
                        R.drawable.circle
                    )
                    container.textView.setPadding(0, 0, 0, 10)

                    if (!events.contains(today)) {
                        container.cardView.setCardBackgroundColor(
                            resources.getColor(
                                R.color.parrotGreen,
                                null
                            )
                        )
                        container.textView.setTextColor(resources.getColor(R.color.white, null))
                        TextViewCompat.setCompoundDrawableTintList(
                            container.textView,
                            ColorStateList.valueOf(resources.getColor(R.color.white, null))
                        )
                    }else if(events.contains(today) &&  ! studyDaysList.contains(dayOfWeek) ) {
                        container.cardView.setCardBackgroundColor(
                            resources.getColor(
                                R.color.parrotGreen,
                                null
                            )
                        )
                        container.textView.setTextColor(resources.getColor(R.color.white, null))
                        TextViewCompat.setCompoundDrawableTintList(
                            container.textView,
                            ColorStateList.valueOf(resources.getColor(R.color.white, null))
                        )
                    } else {
                        container.textView.setTextColor(resources.getColor(R.color.white, null))
                        TextViewCompat.setCompoundDrawableTintList(
                            container.textView,
                            ColorStateList.valueOf(resources.getColor(R.color.parrotGreen, null))
                        )
                    }
                } else {
                    container.textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }
            }

            override fun create(view: View): DayViewContainer {
                return DayViewContainer(view)
            }

        }


        binding.weekCalendarView.weekScrollListener = object : WeekScrollListener {
            override fun invoke(week: Week) {
                // Extract months from the list and remove duplicates
                val uniqueMonths = week.days.map { weekDay -> weekDay.date.month }.distinct()
                val monthText = uniqueMonths.joinToString("/")
                val uniqueYears = week.days.map { weekDay -> weekDay.date.year }.distinct()
                val yearText = uniqueYears.joinToString("/")
                currentDate = week.days[0].date
                val weekMonthYear = "$monthText $yearText"
                binding.yearMonthText.text = weekMonthYear
            }

        }

        binding.prevMonthBtn.setOnClickListener {
            if (binding.calendarView.visibility == View.GONE) {
                currentDate = currentDate.minusWeeks(1)
                binding.weekCalendarView.smoothScrollToWeek(currentDate)
            }

        }

        binding.nextMonthBtn.setOnClickListener {
            if (binding.calendarView.visibility == View.GONE) {
                currentDate = currentDate.plusWeeks(1)
                binding.weekCalendarView.smoothScrollToWeek(currentDate)
            }
        }


    }

    private fun getStudyDaysList(studyDaysString: String): List<String> {
        val studyDaysShortNameList =
            studyDaysString.split(",").filter { item -> item.isNotBlank() }
        Log.d(TAG, studyDaysShortNameList.toString())
        val studyDaysList = mutableListOf<String>()
        for (i in studyDaysShortNameList) {
            val day: String = when (i) {
                "S" -> "Sunday"
                "M" -> "Monday"
                "T" -> "Tuesday"
                "W" -> "Wednesday"
                "H" -> "Thursday"
                "F" -> "Friday"
                "R" -> "Saturday"
                else -> {
                    ""
                }
            }
            studyDaysList.add(day)
        }
        Log.d(TAG, studyDaysList.toString())
        return studyDaysList
    }


    class DayViewContainer(view: View) : ViewContainer(view) {
        val textView = CalendarCellLayoutBinding.bind(view).calendarDayText
        val cardView = CalendarCellLayoutBinding.bind(view).backCard
    }

}