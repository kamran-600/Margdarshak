package com.margdarshakendra.margdarshak.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.margdarshakendra.margdarshak.models.CallRequest
import com.margdarshakendra.margdarshak.models.InductionRequest
import com.margdarshakendra.margdarshak.models.SendEmailRequest
import com.margdarshakendra.margdarshak.models.ShortListUserRequest
import com.margdarshakendra.margdarshak.models.SmsRequest
import com.margdarshakendra.margdarshak.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CounsellingViewModel @Inject constructor(private val dashboardRepository: DashboardRepository) :
    ViewModel() {

    val counsellingPagingFlow = dashboardRepository.getHiringOrHiringFilteredOrCounsellingPagingData("counselling").cachedIn(viewModelScope)

    val counsellingDataResponseLiveData get() = dashboardRepository.counsellingDataResponseLiveData
    val filteredPostsDataLiveData get() = dashboardRepository.filteredPostsDataLiveData

    val shortListUserResponseLiveData get() = dashboardRepository.shortListUserResponseLiveData

    val emailSearchResponseLiveData get() = dashboardRepository.emailSearchLiveData

    val inductionResponseLiveData get() = dashboardRepository.inductionResponseLiveData

    val callResponseLiveData get() = dashboardRepository.callResponseLiveData

    val crmResponseLiveData get() = dashboardRepository.crmResponseLiveData

    val smsResponseLiveData get() = dashboardRepository.smsResponseLiveData

    val emailSmsResponseLiveData get() = dashboardRepository.emailSmsResponseLiveData

    val templateResponseLiveData get() = dashboardRepository.templateResponseLiveData


    fun getCounsellingData(mode: String, position : Int) {
        viewModelScope.launch {
            dashboardRepository.getCounsellingData(mode, position)
        }
    }

    fun getFilterPosts(employerId: Int) {
        viewModelScope.launch {
            dashboardRepository.getFilteredPostsData(employerId)
        }
    }

    fun shortListUser(shortListUserRequest: ShortListUserRequest) {
        viewModelScope.launch {
            dashboardRepository.shortListUser(shortListUserRequest)
        }
    }

    fun emailSearch(email:String) {
        viewModelScope.launch {
            dashboardRepository.searchEmail(email)
        }
    }

    fun inductionRequest(inductionRequest: InductionRequest) {
        viewModelScope.launch {
            dashboardRepository.inductionRequest(inductionRequest)
        }
    }


    fun makeCall(callRequest: CallRequest) {
        viewModelScope.launch {
            dashboardRepository.makeCall(callRequest)
        }
    }

    fun getCrmContact() {
        viewModelScope.launch {
            dashboardRepository.getCrmContact()
        }
    }

    fun sendSms(smsRequest: SmsRequest) {
        viewModelScope.launch {
            dashboardRepository.sendSms(smsRequest)
        }
    }

    fun sendEmailMessage(sendEmailRequest: SendEmailRequest) {
        viewModelScope.launch {
            dashboardRepository.sendEmailMessage(sendEmailRequest)
        }
    }

    fun getTemplateContent(tempID: Int, uid: Int) {
        viewModelScope.launch {
            dashboardRepository.getTemplateContent(tempID, uid)
        }
    }

}