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
class ShowProgressMeterViewModel @Inject constructor(private val dashboardRepository: DashboardRepository) : ViewModel() {

    val showProgressMeterCoursesLiveData get() = dashboardRepository.interactiveCoursesLiveData
    val showProgressMeterSubjectsLiveData get() = dashboardRepository.interactiveSubjectsLiveData
    val progressMeterLiveData get() = dashboardRepository.progressMeterLiveData
    val progressMarksAndTimeLiveData = dashboardRepository.progressMarksAndTimeLiveData


    fun getProgressMeterCoursesRequest(getOrganiserUtilRequest: GetOrganiserUtilRequest) {
        viewModelScope.launch {
            dashboardRepository.getInteractiveCoursesRequest(getOrganiserUtilRequest)
        }
    }

    fun getProgressMeterSubjectsRequest(getOrganiserSubjectsRequest: GetOrganiserSubjectsRequest) {
        viewModelScope.launch {
            dashboardRepository.getInteractiveSubjectsRequest(getOrganiserSubjectsRequest)
        }
    }

    fun getProgressMeterData(getOrganiserSubjectsRequest: GetOrganiserSubjectsRequest){
        viewModelScope.launch {
            dashboardRepository.getProgressMeterData(getOrganiserSubjectsRequest)
        }
    }

    fun getProgressMarksAndTime(getOrganiserLessonsRequest: GetOrganiserLessonsRequest){
        viewModelScope.launch {
            dashboardRepository.getProgressMarksAndTime(getOrganiserLessonsRequest)
        }
    }
}