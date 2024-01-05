package com.margdarshakendra.margdarshak.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.margdarshakendra.margdarshak.models.GetOrganiserSubjectsRequest
import com.margdarshakendra.margdarshak.models.GetOrganiserUtilRequest
import com.margdarshakendra.margdarshak.models.InteractiveTeachersRequest
import com.margdarshakendra.margdarshak.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InteractiveClassViewModel @Inject constructor(private val dashboardRepository: DashboardRepository) :
    ViewModel() {


    val interactiveCoursesLiveData get() = dashboardRepository.interactiveCoursesLiveData

    val interactiveSubjectsLiveData get() = dashboardRepository.interactiveSubjectsLiveData

    val interactiveTeachersLiveData get() = dashboardRepository.interactiveTeachersLiveData

    fun getInteractiveCoursesRequest(getOrganiserUtilRequest: GetOrganiserUtilRequest) {
        viewModelScope.launch {
            dashboardRepository.getInteractiveCoursesRequest(getOrganiserUtilRequest)
        }
    }

    fun getInteractiveSubjectsRequest(getOrganiserSubjectsRequest: GetOrganiserSubjectsRequest) {
        viewModelScope.launch {
            dashboardRepository.getInteractiveSubjectsRequest(getOrganiserSubjectsRequest)
        }
    }

    fun getInteractiveTeachersRequest(interactiveTeachersRequest: InteractiveTeachersRequest) {
        viewModelScope.launch {
            dashboardRepository.getInteractiveTeachersRequest(interactiveTeachersRequest)
        }
    }
}