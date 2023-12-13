package com.margdarshakendra.margdarshak.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.margdarshakendra.margdarshak.models.ClientDataResponse
import com.margdarshakendra.margdarshak.models.DataRequest
import com.margdarshakendra.margdarshak.repository.DashboardRepository
import com.margdarshakendra.margdarshak.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(private val dashboardRepository: DashboardRepository) : ViewModel() {

    val clientDataResponseLiveData: LiveData<NetworkResult<ClientDataResponse>>
        get() = dashboardRepository.clientDataResponseLiveData

    fun getClientData(dataRequest: DataRequest){
        viewModelScope.launch {
            dashboardRepository.getClientData(dataRequest)
        }
    }
}