package com.margdarshakendra.margdarshak.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.margdarshakendra.margdarshak.models.StartAssessmentRequest
import com.margdarshakendra.margdarshak.models.StartAssessmentResponse
import com.margdarshakendra.margdarshak.repository.DashboardRepository
import com.margdarshakendra.margdarshak.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PsychometricAptitudeAssessmentViewModel @Inject constructor(private val dashboardRepository: DashboardRepository) : ViewModel() {
    val startAptitudeAssessmentResponseLiveData: LiveData<NetworkResult<StartAssessmentResponse>>
        get() = dashboardRepository.startAptitudeAssessmentResponseLiveData


    fun getStartAptitudeAssessment(startAssessmentRequest: StartAssessmentRequest) {
        viewModelScope.launch {
            dashboardRepository.getStartAptitudeAssessment(startAssessmentRequest)
        }
    }
}