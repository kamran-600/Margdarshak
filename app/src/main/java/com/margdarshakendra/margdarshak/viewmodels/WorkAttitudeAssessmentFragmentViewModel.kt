package com.margdarshakendra.margdarshak.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.margdarshakendra.margdarshak.models.StartAssessmentRequest
import com.margdarshakendra.margdarshak.models.StartAssessmentResponse
import com.margdarshakendra.margdarshak.models.StartAttitudeAssessmentResponse
import com.margdarshakendra.margdarshak.repository.DashboardRepository
import com.margdarshakendra.margdarshak.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkAttitudeAssessmentFragmentViewModel @Inject constructor(private val dashboardRepository: DashboardRepository) : ViewModel() {

    val startAttitudeAssessmentResponseLiveData: LiveData<NetworkResult<StartAttitudeAssessmentResponse>>
        get() = dashboardRepository.startAttitudeAssessmentResponseLiveData


    fun getStartAttitudeAssessment(startAssessmentRequest: StartAssessmentRequest) {
        viewModelScope.launch {
            dashboardRepository.getStartAttitudeAssessment(startAssessmentRequest)
        }
    }

}