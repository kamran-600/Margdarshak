package com.margdarshakendra.margdarshak.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.margdarshakendra.margdarshak.models.QuestionsResponse
import com.margdarshakendra.margdarshak.models.SaveAptitudeAnswerRequest
import com.margdarshakendra.margdarshak.models.SaveAptitudeAnswerResponse
import com.margdarshakendra.margdarshak.repository.DashboardRepository
import com.margdarshakendra.margdarshak.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PsychometricAptitudeAssessmentQuestionsViewModel @Inject constructor(private val dashboardRepository: DashboardRepository) : ViewModel() {

    val questionsDataResponseLiveData: LiveData<NetworkResult<QuestionsResponse>>
        get() = dashboardRepository.questionsDataResponseLiveData

    val saveAptitudeAnswerResponseLiveData: LiveData<NetworkResult<SaveAptitudeAnswerResponse>>
        get() = dashboardRepository.saveAptitudeAnswerResponseLiveData

    fun getAptitudeQuestions(result_id: Int, page_no: Int){
        viewModelScope.launch {
            dashboardRepository.getAptitudeQuestions(result_id, page_no)
        }
    }

    fun saveAptitudeAnswer(saveAptitudeAnswerRequest: SaveAptitudeAnswerRequest){
        viewModelScope.launch {
            dashboardRepository.saveAptitudeAnswer(saveAptitudeAnswerRequest)
        }
    }

}