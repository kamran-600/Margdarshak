package com.margdarshakendra.margdarshak.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.margdarshakendra.margdarshak.models.LogoutRequest
import com.margdarshakendra.margdarshak.models.LogoutResponse
import com.margdarshakendra.margdarshak.models.SaveFcmTokenRequest
import com.margdarshakendra.margdarshak.models.UserAccessResponse
import com.margdarshakendra.margdarshak.repository.DashboardRepository
import com.margdarshakendra.margdarshak.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(private val dashboardRepository: DashboardRepository) :
    ViewModel() {

    val userAccessResponseLiveData: LiveData<NetworkResult<UserAccessResponse>>
        get() = dashboardRepository.userAccessResponseLiveData

    val logoutResponseLiveData: LiveData<NetworkResult<LogoutResponse>>
        get() = dashboardRepository.logoutResponseLiveData

    val saveFcmTokenLiveData get() = dashboardRepository.saveFcmTokenLiveData


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

}