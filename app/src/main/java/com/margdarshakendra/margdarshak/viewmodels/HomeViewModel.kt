package com.margdarshakendra.margdarshak.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.margdarshakendra.margdarshak.models.ClientDataResponse
import com.margdarshakendra.margdarshak.models.DataRequest
import com.margdarshakendra.margdarshak.models.GetForCounsellingRequest
import com.margdarshakendra.margdarshak.models.ShortListUserRequest
import com.margdarshakendra.margdarshak.repository.DashboardRepository
import com.margdarshakendra.margdarshak.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(private val dashboardRepository: DashboardRepository) : ViewModel() {

    val clientDataResponseLiveData get() = dashboardRepository.clientDataResponseLiveData
    val getForCounsellingResponseLiveData get() = dashboardRepository.getForCounsellingResponseLiveData
    val employerPostsDataLiveData get()  = dashboardRepository.filteredPostsDataLiveData
    val shortListUserResponseLiveData get() = dashboardRepository.shortListUserResponseLiveData

    fun getClientData(mode: String){
        viewModelScope.launch {
            dashboardRepository.getClientData(mode)
        }
    }

    fun getForCounselling(getForCounsellingRequest: GetForCounsellingRequest){
        viewModelScope.launch {
            dashboardRepository.getForCounselling(getForCounsellingRequest)
        }
    }

    fun getEmployerPostsData(employerId: Int) {
        viewModelScope.launch {
            dashboardRepository.getFilteredPostsData(employerId)
        }
    }

    fun shortListUser(shortListUserRequest: ShortListUserRequest) {
        viewModelScope.launch {
            dashboardRepository.shortListUser(shortListUserRequest)
        }
    }
}