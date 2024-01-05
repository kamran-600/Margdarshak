package com.margdarshakendra.margdarshak.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.margdarshakendra.margdarshak.models.EditOrganisedStudyScheduleResponse
import com.margdarshakendra.margdarshak.models.GetOrganisedStudySchedulesResponse
import com.margdarshakendra.margdarshak.models.GetOrganiserLessonsRequest
import com.margdarshakendra.margdarshak.models.GetOrganiserStudyTimeRequest
import com.margdarshakendra.margdarshak.models.GetOrganiserSubjectsRequest
import com.margdarshakendra.margdarshak.models.GetOrganiserUtilRequest
import com.margdarshakendra.margdarshak.models.OrganisedStudyScheduleRequest
import com.margdarshakendra.margdarshak.models.OrganisedStudyScheduleResponse
import com.margdarshakendra.margdarshak.repository.DashboardRepository
import com.margdarshakendra.margdarshak.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudyOrganiserViewModel @Inject constructor(private val dashboardRepository: DashboardRepository) :
    ViewModel() {

    val organiserCoursesLiveData get() = dashboardRepository.organiserCoursesLiveData
    val organiserWeightagesLiveData get() = dashboardRepository.organiserWeightagesLiveData
    val organiserLanguagesLiveData get() = dashboardRepository.organiserLanguagesLiveData
    val organiserSubjectsLiveData get() = dashboardRepository.organiserSubjectsLiveData
    val organiserLessonsLiveData get() = dashboardRepository.organiserLessonsLiveData
    val organiserStudyTimeLiveData get() = dashboardRepository.organiserStudyTimeLiveData

    val organisedStudySchedulesLiveData get() = dashboardRepository.organisedStudySchedulesLiveData
    val scheduleOrganisedStudyLiveData get() = dashboardRepository.scheduleOrganisedStudyLiveData
    val editScheduledOrganisedStudyLiveData get() = dashboardRepository.editScheduledOrganisedStudyLiveData

    fun getOrganiserCourses(getOrganiserUtilRequest: GetOrganiserUtilRequest) {
        viewModelScope.launch {
            dashboardRepository.getOrganiserCourses(getOrganiserUtilRequest)
        }
    }

    fun getOrganiserWeightages(getOrganiserUtilRequest: GetOrganiserUtilRequest) {
        viewModelScope.launch {
            dashboardRepository.getOrganiserWeightages(getOrganiserUtilRequest)
        }
    }

    fun getOrganiserLanguages(getOrganiserUtilRequest: GetOrganiserUtilRequest) {
        viewModelScope.launch {
            dashboardRepository.getOrganiserLanguages(getOrganiserUtilRequest)
        }
    }

    fun getOrganiserSubjects(getOrganiserSubjectsRequest: GetOrganiserSubjectsRequest) {
        viewModelScope.launch {
            dashboardRepository.getOrganiserSubjects(getOrganiserSubjectsRequest)
        }
    }

    fun getOrganiserLessons(getOrganiserLessonsRequest: GetOrganiserLessonsRequest) {
        viewModelScope.launch {
            dashboardRepository.getOrganiserLessons(getOrganiserLessonsRequest)
        }
    }

    fun getOrganiserStudyTime(getOrganiserStudyTimeRequest: GetOrganiserStudyTimeRequest) {
        viewModelScope.launch {
            dashboardRepository.getOrganiserStudyTime(getOrganiserStudyTimeRequest)
        }
    }

    fun getOrganisedStudySchedules(){
        viewModelScope.launch {
            dashboardRepository.getOrganisedStudySchedules()
        }
    }

    fun scheduleOrganisedStudy(organisedStudyScheduleRequest: OrganisedStudyScheduleRequest){
        viewModelScope.launch {
            dashboardRepository.scheduleOrganisedStudy(organisedStudyScheduleRequest)
        }
    }

    fun editOrganisedStudySchedules(scheduleID : Int){
        viewModelScope.launch {
            dashboardRepository.editOrganisedStudySchedules(scheduleID)
        }
    }
}