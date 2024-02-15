package com.margdarshakendra.margdarshak.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.margdarshakendra.margdarshak.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.http.Part
import javax.inject.Inject

@HiltViewModel
class DocsUploadViewModel @Inject constructor(private val dashboardRepository: DashboardRepository): ViewModel() {

    val submitDocsUploadLiveData get() = dashboardRepository.submitDocsUploadLiveData

    fun submitDocsUpload(@Part photoId: MultipartBody.Part, @Part panCard: MultipartBody.Part?= null, @Part hac: MultipartBody.Part?= null, @Part pc: MultipartBody.Part?= null, @Part lac: MultipartBody.Part?=null){
        viewModelScope.launch {
            dashboardRepository.submitDocsUpload(photoId, panCard, hac, pc, lac)
        }
    }
}