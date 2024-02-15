package com.margdarshakendra.margdarshak.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.margdarshakendra.margdarshak.models.GetComparisonDataRequest
import com.margdarshakendra.margdarshak.models.GetOrganiserLessonsRequest
import com.margdarshakendra.margdarshak.models.GetOrganiserSubjectsRequest
import com.margdarshakendra.margdarshak.models.GetOrganiserUtilRequest
import com.margdarshakendra.margdarshak.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompareViewModel @Inject constructor(private val dashboardRepository: DashboardRepository) :
    ViewModel() {

    val compareCoursesLiveData get() = dashboardRepository.interactiveCoursesLiveData
    val compareSubjectsLiveData get() = dashboardRepository.interactiveSubjectsLiveData
    val compareLessonsLiveData get() = dashboardRepository.resultLessonsLiveData

    val emailSearchLiveData get() = dashboardRepository.emailSearchLiveData
    val comparisonLiveData get() = dashboardRepository.comparisonLiveData


    fun getCompareCoursesRequest(getOrganiserUtilRequest: GetOrganiserUtilRequest) {
        viewModelScope.launch {
            dashboardRepository.getInteractiveCoursesRequest(getOrganiserUtilRequest)
        }
    }

    fun getCompareSubjectsRequest(getOrganiserSubjectsRequest: GetOrganiserSubjectsRequest) {
        viewModelScope.launch {
            dashboardRepository.getInteractiveSubjectsRequest(getOrganiserSubjectsRequest)
        }
    }

    fun getCompareLessonsRequest(getOrganiserLessonsRequest: GetOrganiserLessonsRequest) {
        viewModelScope.launch {
            dashboardRepository.getResultLessons(getOrganiserLessonsRequest)
        }
    }

    fun searchEmail(email: String) {
        viewModelScope.launch {
            dashboardRepository.searchEmail(email)
        }
    }

    fun getComparisonDataRequest(getComparisonDataRequest: GetComparisonDataRequest) {
        viewModelScope.launch {
            dashboardRepository.getComparisonDataRequest(getComparisonDataRequest)
        }
    }


}