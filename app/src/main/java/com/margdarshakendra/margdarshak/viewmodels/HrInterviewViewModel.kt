package com.margdarshakendra.margdarshak.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.margdarshakendra.margdarshak.models.HrInterviewQuesUtilRequest
import com.margdarshakendra.margdarshak.models.StartHrInterviewRequest
import com.margdarshakendra.margdarshak.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HrInterviewViewModel @Inject constructor(private val dashboardRepository: DashboardRepository) : ViewModel() {

    val startHrInterviewLiveData get() = dashboardRepository.startHrInterviewLiveData

    val hrInterviewQuesLiveData = dashboardRepository.hrInterviewQuesLiveData

    val hrInterviewQuesUtilsLiveData = dashboardRepository.hrInterviewQuesUtilsLiveData


    fun startHrInterviewRequest(startHrInterviewRequest: StartHrInterviewRequest){
        viewModelScope.launch {
            dashboardRepository.startHrInterviewRequest(startHrInterviewRequest)
        }
    }

    fun getHrInterviewQuestions(resultId : Int){
        viewModelScope.launch {
            dashboardRepository.getHrInterviewQuestions(resultId)
        }
    }

    fun setHrInterviewQuesData(hrInterviewQuesUtilRequest: HrInterviewQuesUtilRequest){
        viewModelScope.launch {
            dashboardRepository.setHrInterviewQuesData(hrInterviewQuesUtilRequest)
        }
    }

}