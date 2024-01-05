package com.margdarshakendra.margdarshak.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.margdarshakendra.margdarshak.models.GetOrganiserUtilRequest
import com.margdarshakendra.margdarshak.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalenderViewModel @Inject constructor(private val dashboardRepository: DashboardRepository) : ViewModel() {

    val calenderSchedulesLiveData = dashboardRepository.calenderSchedulesLiveData

    fun getCalenderSchedules(getOrganiserUtilRequest: GetOrganiserUtilRequest){
        viewModelScope.launch {
            dashboardRepository.getCalenderSchedules(getOrganiserUtilRequest)
        }
    }

}