package com.margdarshakendra.margdarshak.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.margdarshakendra.margdarshak.models.GetInteractiveQuestionRequest
import com.margdarshakendra.margdarshak.models.StartInteractiveTestRequest
import com.margdarshakendra.margdarshak.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.http.Body
import javax.inject.Inject

@HiltViewModel
class InteractiveLessonTestViewModel @Inject constructor(private val dashboardRepository: DashboardRepository) :
    ViewModel() {

    val interactiveTestDetailsLiveData get() = dashboardRepository.interactiveTestDetailsLiveData
    val startInteractiveTestLiveData get() = dashboardRepository.startInteractiveTestLiveData



    fun getInteractiveTestDetails(lessonId: Int) {
        viewModelScope.launch {
            dashboardRepository.getInteractiveTestDetailsRequest(lessonId)
        }
    }

    fun startInteractiveTestRequest(lessonId: Int, startInteractiveTestRequest: StartInteractiveTestRequest) {
        viewModelScope.launch {
            dashboardRepository.startInteractiveTestRequest(lessonId, startInteractiveTestRequest)
        }
    }

}