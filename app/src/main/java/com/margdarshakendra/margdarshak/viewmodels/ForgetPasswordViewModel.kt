package com.margdarshakendra.margdarshak.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.margdarshakendra.margdarshak.models.ForgetPasswordRequest
import com.margdarshakendra.margdarshak.repository.ForgetPasswordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgetPasswordViewModel @Inject constructor(private val forgetPasswordRepository: ForgetPasswordRepository) :
    ViewModel() {

    val forgetPasswordResponseLiveData get() = forgetPasswordRepository.forgetPasswordResponseLiveData

    fun forgetPasswordRequest(forgetPasswordRequest: ForgetPasswordRequest) {
        viewModelScope.launch {
            forgetPasswordRepository.forgetPasswordRequest(forgetPasswordRequest)
        }
    }
}