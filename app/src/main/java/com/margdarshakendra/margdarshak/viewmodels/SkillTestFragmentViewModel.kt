package com.margdarshakendra.margdarshak.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.margdarshakendra.margdarshak.models.SkillResponse
import com.margdarshakendra.margdarshak.models.StartSkillTestRequest
import com.margdarshakendra.margdarshak.models.StartSkillTestResponse
import com.margdarshakendra.margdarshak.repository.DashboardRepository
import com.margdarshakendra.margdarshak.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SkillTestFragmentViewModel @Inject constructor(private val dashboardRepository: DashboardRepository) : ViewModel() {
    val skillsResponseLiveData: LiveData<NetworkResult<SkillResponse>>
        get() = dashboardRepository.skillsResponseLiveData

    val startSkillTestResponseLiveData: LiveData<NetworkResult<StartSkillTestResponse>>
        get() = dashboardRepository.startSkillTestResponseLiveData

    fun getSkills() {
        viewModelScope.launch {
            dashboardRepository.getSkills()
        }
    }

    fun getStartSkillTest(startSkillTestRequest: StartSkillTestRequest) {
        viewModelScope.launch {
            dashboardRepository.getStartSkillTest(startSkillTestRequest)
        }
    }
}