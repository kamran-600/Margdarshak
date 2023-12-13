package com.margdarshakendra.margdarshak.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.margdarshakendra.margdarshak.models.CRMResponse
import com.margdarshakendra.margdarshak.models.CallRequest
import com.margdarshakendra.margdarshak.models.CallResponse
import com.margdarshakendra.margdarshak.models.CounsellingDataResponse
import com.margdarshakendra.margdarshak.models.DataRequest
import com.margdarshakendra.margdarshak.models.SendEmailRequest
import com.margdarshakendra.margdarshak.models.SmsRequest
import com.margdarshakendra.margdarshak.models.SmsResponse
import com.margdarshakendra.margdarshak.models.TemplateResponse
import com.margdarshakendra.margdarshak.repository.DashboardRepository
import com.margdarshakendra.margdarshak.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CounsellingViewModel @Inject constructor(private val dashboardRepository: DashboardRepository) :
    ViewModel() {

    val counsellingDataResponseLiveData: LiveData<NetworkResult<CounsellingDataResponse>>
        get() = dashboardRepository.counsellingDataResponseLiveData

    val callResponseLiveData: LiveData<NetworkResult<CallResponse>>
        get() = dashboardRepository.callResponseLiveData

    val crmResponseLiveData: LiveData<NetworkResult<CRMResponse>>
        get() = dashboardRepository.crmResponseLiveData

    val smsResponseLiveData: LiveData<NetworkResult<SmsResponse>>
        get() = dashboardRepository.smsResponseLiveData

    val emailSmsResponseLiveData: LiveData<NetworkResult<SmsResponse>>
        get() = dashboardRepository.emailSmsResponseLiveData

    val templateResponseLiveData: LiveData<NetworkResult<TemplateResponse>>
        get() = dashboardRepository.templateResponseLiveData


    fun getCounsellingData(dataRequest: DataRequest) {
        viewModelScope.launch {
            dashboardRepository.getCounsellingData(dataRequest)
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