package com.margdarshakendra.margdarshak.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.margdarshakendra.margdarshak.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject

@HiltViewModel
class HrInterviewIntervieweeViewModel @Inject constructor(private val dashboardRepository: DashboardRepository) : ViewModel() {

    val hrIntervieweeQuesLiveData get() = dashboardRepository.hrIntervieweeQuesLiveData

    fun getHrIntervieweeQues() {
        viewModelScope.launch {
            dashboardRepository.getHrIntervieweeQues()
        }
    }


}