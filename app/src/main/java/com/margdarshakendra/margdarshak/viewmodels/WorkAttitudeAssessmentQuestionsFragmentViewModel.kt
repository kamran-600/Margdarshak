package com.margdarshakendra.margdarshak.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.margdarshakendra.margdarshak.models.AttitudeQuestionsResponse
import com.margdarshakendra.margdarshak.models.QuestionsResponse
import com.margdarshakendra.margdarshak.models.SaveAptitudeAnswerRequest
import com.margdarshakendra.margdarshak.models.SaveAptitudeAnswerResponse
import com.margdarshakendra.margdarshak.models.SaveAttitudeQuestionsRequest
import com.margdarshakendra.margdarshak.models.SaveAttitudeQuestionsResponse
import com.margdarshakendra.margdarshak.repository.DashboardRepository
import com.margdarshakendra.margdarshak.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkAttitudeAssessmentQuestionsFragmentViewModel @Inject constructor(private val dashboardRepository: DashboardRepository) : ViewModel() {

    val attitudeQuestionsDataResponseLiveData: LiveData<NetworkResult<AttitudeQuestionsResponse>>
        get() = dashboardRepository.attitudeQuestionsDataResponseLiveData

    fun getAttitudeQuestions(page_no: Int) {
        viewModelScope.launch {
            dashboardRepository.getAttitudeQuestions( page_no)
        }
    }

    val saveAttitudeQuestionsResponseLiveData: LiveData<NetworkResult<SaveAttitudeQuestionsResponse>>
        get() = dashboardRepository.saveAttitudeQuestionsResponseLiveData


    fun saveAttitudeQuestions(saveAttitudeQuestionsRequest: SaveAttitudeQuestionsRequest){
        viewModelScope.launch {
            dashboardRepository.saveAttitudeQuestions(saveAttitudeQuestionsRequest)
        }
    }

}