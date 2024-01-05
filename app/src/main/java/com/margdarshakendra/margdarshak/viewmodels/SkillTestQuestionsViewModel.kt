package com.margdarshakendra.margdarshak.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.margdarshakendra.margdarshak.models.SaveSkillTestAnswerRequest
import com.margdarshakendra.margdarshak.models.SaveSkillTestAnswerResponse
import com.margdarshakendra.margdarshak.models.SkillTestQuestionResponse
import com.margdarshakendra.margdarshak.models.UpdateTimerRequest
import com.margdarshakendra.margdarshak.models.UpdateTimerResponse
import com.margdarshakendra.margdarshak.repository.DashboardRepository
import com.margdarshakendra.margdarshak.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SkillTestQuestionsViewModel @Inject constructor(private val dashboardRepository: DashboardRepository) :
    ViewModel() {

    val skillTestQuestionResponseLiveData: LiveData<NetworkResult<SkillTestQuestionResponse>>
        get() = dashboardRepository.skillTestQuestionResponseLiveData

    val updateTimerResponseLiveData: LiveData<NetworkResult<UpdateTimerResponse>>
        get() = dashboardRepository.updateTimerResponseLiveData

    val saveSkillTestAnswerResponseLiveData: LiveData<NetworkResult<SaveSkillTestAnswerResponse>>
        get() = dashboardRepository.saveSkillTestAnswerResponseLiveData


    fun getSkillTestQuestions(result_id: Int, page_no: Int) {
        viewModelScope.launch {
            dashboardRepository.getSkillTestQuestions(result_id, page_no)
        }
    }

    fun updateTimer(updateTimerRequest: UpdateTimerRequest) {
        viewModelScope.launch {
            dashboardRepository.updateTimer(updateTimerRequest)
        }
    }

    fun saveSkillTestAnswer(saveSkillTestAnswerRequest: SaveSkillTestAnswerRequest) {
        viewModelScope.launch {
            dashboardRepository.saveSkillTestAnswer(saveSkillTestAnswerRequest)
        }
    }


}