package com.margdarshakendra.margdarshak.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.margdarshakendra.margdarshak.models.GetOrganiserLessonsRequest
import com.margdarshakendra.margdarshak.models.GetOrganiserSubjectsRequest
import com.margdarshakendra.margdarshak.models.GetOrganiserUtilRequest
import com.margdarshakendra.margdarshak.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PerformanceViewModel @Inject constructor(private val dashboardRepository: DashboardRepository) :
    ViewModel() {

    val performanceCoursesLiveData get() = dashboardRepository.interactiveCoursesLiveData

    val performanceSubjectsLiveData get() = dashboardRepository.interactiveSubjectsLiveData

    val subjectWisePerformanceLiveData get() = dashboardRepository.subjectWisePerformanceLiveData

    val lessonWisePerformanceLiveData get() = dashboardRepository.lessonWisePerformanceLiveData

    fun getPerformanceCoursesRequest(getOrganiserUtilRequest: GetOrganiserUtilRequest) {
        viewModelScope.launch {
            dashboardRepository.getInteractiveCoursesRequest(getOrganiserUtilRequest)
        }
    }

    fun getPerformanceSubjectsRequest(getOrganiserSubjectsRequest: GetOrganiserSubjectsRequest) {
        viewModelScope.launch {
            dashboardRepository.getInteractiveSubjectsRequest(getOrganiserSubjectsRequest)
        }
    }


    fun getSubjectWisePerformance(getOrganiserUtilRequest: GetOrganiserUtilRequest) {
        viewModelScope.launch {
            dashboardRepository.getSubjectWisePerformance(getOrganiserUtilRequest)
        }
    }


    fun getLessonWisePerformance(getOrganiserLessonsRequest: GetOrganiserLessonsRequest) {
        viewModelScope.launch {
            dashboardRepository.getLessonWisePerformance(getOrganiserLessonsRequest)
        }
    }


}