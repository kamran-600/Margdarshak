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
class ResultsViewModel @Inject constructor(private val dashboardRepository: DashboardRepository) :
    ViewModel() {

    val resultCoursesLiveData get() = dashboardRepository.interactiveCoursesLiveData
    val resultSubjectsLiveData get() = dashboardRepository.interactiveSubjectsLiveData
    val resultLessonsLiveData get() = dashboardRepository.resultLessonsLiveData
    val mcqResultsLiveData get() = dashboardRepository.mcqResultsLiveData

    fun getResultCoursesRequest(getOrganiserUtilRequest: GetOrganiserUtilRequest) {
        viewModelScope.launch {
            dashboardRepository.getInteractiveCoursesRequest(getOrganiserUtilRequest)
        }
    }

    fun getResultSubjectsRequest(getOrganiserSubjectsRequest: GetOrganiserSubjectsRequest) {
        viewModelScope.launch {
            dashboardRepository.getInteractiveSubjectsRequest(getOrganiserSubjectsRequest)
        }
    }

    fun getResultLessonsRequest(getOrganiserLessonsRequest: GetOrganiserLessonsRequest) {
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