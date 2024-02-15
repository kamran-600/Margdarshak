package com.margdarshakendra.margdarshak.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.margdarshakendra.margdarshak.models.ScheduleNotificationRequest
import com.margdarshakendra.margdarshak.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleNotificationViewModel @Inject constructor(private val dashboardRepository: DashboardRepository) :ViewModel() {

    val scheduleNotificationLiveData get() = dashboardRepository.scheduleNotificationLiveData


    fun scheduleNotification(scheduleNotificationRequest: ScheduleNotificationRequest){
        viewModelScope.launch {
            dashboardRepository.scheduleNotification(scheduleNotificationRequest)
        }
    }

}