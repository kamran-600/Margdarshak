package com.margdarshakendra.margdarshak.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.margdarshakendra.margdarshak.models.GetOrganiserLessonsRequest
import com.margdarshakendra.margdarshak.models.GetOrganiserSubjectsRequest
import com.margdarshakendra.margdarshak.models.GetOrganiserUtilRequest
import com.margdarshakendra.margdarshak.models.McqResultsRequest
import com.margdarshakendra.margdarshak.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EfficiencyViewModel @Inject constructor(private val dashboardRepository: DashboardRepository) : ViewModel() {

    val efficiencyCoursesLiveData get() = dashboardRepository.interactiveCoursesLiveData
    val efficiencySubjectsLiveData get() = dashboardRepository.interactiveSubjectsLiveData
    val efficiencyLessonsLiveData get() = dashboardRepository.resultLessonsLiveData
    val mcqResultsLiveData get() = dashboardRepository.mcqResultsLiveData

    fun getEfficiencyCoursesRequest(getOrganiserUtilRequest: GetOrganiserUtilRequest) {
        viewModelScope.launch {
            dashboardRepository.getInteractiveCoursesRequest(getOrganiserUtilRequest)
        }
    }

    fun getEfficiencySubjectsRequest(getOrganiserSubjectsRequest: GetOrganiserSubjectsRequest) {
        viewModelScope.launch {
            dashboardRepository.getInteractiveSubjectsRequest(getOrganiserSubjectsRequest)
        }
    }

    fun getEfficiencyLessonsRequest(getOrganiserLessonsRequest: GetOrganiserLessonsRequest) {
        viewModelScope.launch {
            dashboardRepository.getResultLessons(getOrganiserLessonsRequest)
        }
    }

    fun getMcqResults(mcqResultsRequest: McqResultsRequest) {
        viewModelScope.launch {
            dashboardRepository.getMcqResults(mcqResultsRequest)
        }
    }
}