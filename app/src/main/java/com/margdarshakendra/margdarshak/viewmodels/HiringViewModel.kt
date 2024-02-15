package com.margdarshakendra.margdarshak.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.margdarshakendra.margdarshak.models.CallRequest
import com.margdarshakendra.margdarshak.models.GiveCommunicationTestLinkRequest
import com.margdarshakendra.margdarshak.models.GiveDocsUploadLinkRequest
import com.margdarshakendra.margdarshak.models.GiveSkillTestLinkRequest
import com.margdarshakendra.margdarshak.models.InductionRequest
import com.margdarshakendra.margdarshak.models.SendEmailRequest
import com.margdarshakendra.margdarshak.models.ShortListUserRequest
import com.margdarshakendra.margdarshak.models.SmsRequest
import com.margdarshakendra.margdarshak.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HiringViewModel @Inject constructor(private val dashboardRepository: DashboardRepository) :
    ViewModel() {


    val hiringDataResponseLiveData get() = dashboardRepository.hiringDataResponseLiveData

    val hiringFilteredDataLiveData get()  = dashboardRepository.hiringFilteredDataLiveData

    val filteredPostsDataLiveData get()  = dashboardRepository.filteredPostsDataLiveData

    val shortListUserResponseLiveData get() = dashboardRepository.shortListUserResponseLiveData

    val hiringSkillsLiveData get() = dashboardRepository.hiringSkillsLiveData
    val giveHiringSkillTestLinkLiveData get() = dashboardRepository.giveHiringSkillTestLinkLiveData
    val giveHiringInterviewTestLinkLiveData get() = dashboardRepository.giveHiringInterviewTestLinkLiveData
    val communicationTestsLiveData get() = dashboardRepository.communicationTestsLiveData
    val giveCommunicationTestLinkResponseLiveData get() = dashboardRepository.giveCommunicationTestLinkResponseLiveData
    val giveDocsUploadLinkResponseLiveData get() = dashboardRepository.giveDocsUploadLinkResponseLiveData

    val emailSearchResponseLiveData get() = dashboardRepository.emailSearchLiveData

    val inductionResponseLiveData get() = dashboardRepository.inductionResponseLiveData

    val callResponseLiveData get() = dashboardRepository.callResponseLiveData

    val crmResponseLiveData get() = dashboardRepository.crmResponseLiveData

    val smsResponseLiveData get() = dashboardRepository.smsResponseLiveData

    val emailSmsResponseLiveData get() = dashboardRepository.emailSmsResponseLiveData

    val templateResponseLiveData get() = dashboardRepository.templateResponseLiveData


    fun hiringOrFilteredPagingFlow(mode : String?= null, employerId: Int?= null, postId: Int?= null) = dashboardRepository.getHiringOrHiringFilteredOrCounsellingPagingData(mode, employerId, postId).cachedIn(viewModelScope)


    fun getHiringData(mode:String,pageNo: Int) {
        viewModelScope.launch {
            dashboardRepository.getHiringData(mode, pageNo)
        }
    }

  /*  fun getHiringFilteredData(employerId: Int, postId: Int) {
        viewModelScope.launch {
            dashboardRepository.getHiringFilteredData(employerId, postId)
        }
    }
*/
    fun getFilteredPostsData(employerId: Int) {
        viewModelScope.launch {
            dashboardRepository.getFilteredPostsData(employerId)
        }
    }

    fun shortListUser(shortListUserRequest: ShortListUserRequest) {
        viewModelScope.launch {
            dashboardRepository.shortListUser(shortListUserRequest)
        }
    }

    fun getHiringSkills() {
        viewModelScope.launch {
            dashboardRepository.getHiringSkills()
        }
    }

    fun giveHiringSkillTestLink(giveSkillTestLinkRequest : GiveSkillTestLinkRequest) {
        viewModelScope.launch {
            dashboardRepository.giveHiringSkillTestLink(giveSkillTestLinkRequest)
        }
    }

    fun giveHiringInterviewLink(giveSkillTestLinkRequest : GiveSkillTestLinkRequest) {
        viewModelScope.launch {
            dashboardRepository.giveHiringInterviewLink(giveSkillTestLinkRequest)
        }
    }

    fun getCommunicationTests() {
        viewModelScope.launch {
            dashboardRepository.getCommunicationTests()
        }
    }

    fun giveCommunicationTestLink(giveCommunicationTestLinkRequest: GiveCommunicationTestLinkRequest) {
        viewModelScope.launch {
            dashboardRepository.giveCommunicationTestLink(giveCommunicationTestLinkRequest)
        }
    }

    fun giveDocsUploadLink(giveDocsUploadLinkRequest: GiveDocsUploadLinkRequest) {
        viewModelScope.launch {
            dashboardRepository.giveDocsUploadLink(giveDocsUploadLinkRequest)
        }
    }

    fun emailSearch(email:String) {
        viewModelScope.launch {
            dashboardRepository.searchEmail(email)
        }
    }

    fun inductionRequest(inductionRequest:InductionRequest) {
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