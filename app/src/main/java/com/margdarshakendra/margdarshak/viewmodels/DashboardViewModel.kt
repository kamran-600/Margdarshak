package com.margdarshakendra.margdarshak.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.margdarshakendra.margdarshak.models.LogoutRequest
import com.margdarshakendra.margdarshak.models.SaveFcmTokenRequest
import com.margdarshakendra.margdarshak.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(private val dashboardRepository: DashboardRepository) :
    ViewModel() {

    val userAccessResponseLiveData get() = dashboardRepository.userAccessResponseLiveData

    val logoutResponseLiveData get() = dashboardRepository.logoutResponseLiveData

    val saveFcmTokenLiveData get() = dashboardRepository.saveFcmTokenLiveData

    val deleteNotificationLiveData get() = dashboardRepository.deleteNotificationLiveData


    fun getUserAccess() {
        viewModelScope.launch {
            dashboardRepository.getUserAccess()
        }
    }

    fun logout(logoutRequest: LogoutRequest) {
        viewModelScope.launch {
            dashboardRepository.logout(logoutRequest)
        }
    }

    fun saveFcmTokenRequest(saveFcmTokenRequest: SaveFcmTokenRequest) {
        viewModelScope.launch {
            dashboardRepository.saveFcmTokenRequest(saveFcmTokenRequest)
        }
    }

    fun deleteNotification(notifyId : Int) {
        viewModelScope.launch {
            dashboardRepository.deleteNotification(notifyId)
        }
    }

}