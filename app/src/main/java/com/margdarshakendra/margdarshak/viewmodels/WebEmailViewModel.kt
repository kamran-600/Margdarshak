package com.margdarshakendra.margdarshak.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.margdarshakendra.margdarshak.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WebEmailViewModel @Inject constructor(private val dashboardRepository: DashboardRepository) :ViewModel() {

    val webEmailsLiveData get() = dashboardRepository.webEmailsLiveData
    val importedWebEmailsLiveData get() = dashboardRepository.importedWebEmailsLiveData

    fun getWebEmails(category :String){
        viewModelScope.launch {
            dashboardRepository.getWebEmails(category)
        }
    }

    fun getImportedWebEmails(){
        viewModelScope.launch {
            dashboardRepository.getImportedWebEmails()
        }
    }


}