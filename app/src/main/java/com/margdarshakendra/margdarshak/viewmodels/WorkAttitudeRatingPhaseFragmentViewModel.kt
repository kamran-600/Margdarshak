package com.margdarshakendra.margdarshak.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.margdarshakendra.margdarshak.models.AttitudeQuestionsResponse
import com.margdarshakendra.margdarshak.models.AttitudeRatingQuesResponse
import com.margdarshakendra.margdarshak.models.QuestionsResponse
import com.margdarshakendra.margdarshak.models.SaveAttitudeRatingQuesRequest
import com.margdarshakendra.margdarshak.models.SaveAttitudeRatingQuesResponse
import com.margdarshakendra.margdarshak.repository.DashboardRepository
import com.margdarshakendra.margdarshak.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkAttitudeRatingPhaseFragmentViewModel @Inject constructor(private val dashboardRepository: DashboardRepository ) : ViewModel() {

    val attitudeRatingQuestionsDataResponseLiveData: LiveData<NetworkResult<AttitudeRatingQuesResponse>>
        get() = dashboardRepository.attitudeRatingQuestionsDataResponseLiveData

    val saveAttitudeRatingQuesAndFinishTestResponseLiveData: LiveData<NetworkResult<SaveAttitudeRatingQuesResponse>>
        get() = dashboardRepository.saveAttitudeRatingQuesAndFinishTestResponseLiveData

    fun getAttitudeRatingQuestions(result_id: Int) {
        viewModelScope.launch {
            dashboardRepository.getAttitudeRatingQuestions(result_id)
        }
    }

    fun saveAttitudeRatingQuesAndFinishTest(result_id: Int, saveAttitudeRatingQuesRequest: SaveAttitudeRatingQuesRequest){
        viewModelScope.launch {
            dashboardRepository.saveAttitudeRatingQuesAndFinishTest(result_id, saveAttitudeRatingQuesRequest)
        }
    }
}