package com.margdarshakendra.margdarshak.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.margdarshakendra.margdarshak.models.UpdateCommunicationTimerRequest
import com.margdarshakendra.margdarshak.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class CommunicationTestViewModel @Inject constructor(private val dashboardRepository: DashboardRepository): ViewModel() {

    val hireTestIdLiveData get() = dashboardRepository.hireTestIdLiveData
    val updateCommunicationTimerLiveData get() = dashboardRepository.updateCommunicationTimerLiveData
    val submitCommunicationTestLiveData get() = dashboardRepository.submitCommunicationTestLiveData

    fun getHireTestId(){
        viewModelScope.launch {
            dashboardRepository.getHireTestId()
        }
    }

    fun updateCommunicationTestTimer(updateCommunicationTimerRequest: UpdateCommunicationTimerRequest){
        viewModelScope.launch {
            dashboardRepository.updateCommunicationTestTimer(updateCommunicationTimerRequest)
        }
    }


    fun submitCommunicationTest(hiretestID: RequestBody, answer: RequestBody, fileupload: MultipartBody.Part){
        viewModelScope.launch {
            dashboardRepository.submitCommunicationTest(hiretestID, answer, fileupload)
        }
    }


}