package com.margdarshakendra.margdarshak.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.margdarshakendra.margdarshak.models.GetInteractiveQuestionRequest
import com.margdarshakendra.margdarshak.models.SaveInteractiveAnswerRequest
import com.margdarshakendra.margdarshak.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InteractiveTestQuestionsViewModel @Inject constructor(private val dashboardRepository: DashboardRepository) : ViewModel() {

    val interactiveTestQuestionLiveData get() = dashboardRepository.interactiveTestQuestionLiveData
    val saveInteractiveAnsResponseLiveData get() = dashboardRepository.saveInteractiveAnsResponseLiveData

    fun getInteractiveQuestions(resultId: Int, quesNo: Int) {
        viewModelScope.launch {
            dashboardRepository.getInteractiveQuestions(resultId, quesNo)
        }
    }

    fun saveInteractiveAnswerRequest(saveInteractiveQuestionRequest: SaveInteractiveAnswerRequest) {
        viewModelScope.launch {
            dashboardRepository.saveInteractiveAnswerRequest(saveInteractiveQuestionRequest)
        }
    }
}