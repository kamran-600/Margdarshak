package com.margdarshakendra.margdarshak.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.margdarshakendra.margdarshak.models.OtpRequest
import com.margdarshakendra.margdarshak.models.OtpResponse
import com.margdarshakendra.margdarshak.models.RegisterRequest
import com.margdarshakendra.margdarshak.models.RegisterResponse
import com.margdarshakendra.margdarshak.repository.RegisterRepository
import com.margdarshakendra.margdarshak.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RegisterViewModel @Inject constructor(private val registerRepository: RegisterRepository) : ViewModel() {

    val registerResponseLiveData: LiveData<NetworkResult<RegisterResponse>>
        get() = registerRepository.registerResponseLiveData

    val otpResponseLiveData: LiveData<NetworkResult<OtpResponse>>
        get() = registerRepository.otpResponseLiveData


    fun registerUser( registerRequest: RegisterRequest) {
        viewModelScope.launch {
                registerRepository.registerUser(registerRequest)
        }
    }

    fun sendOtp(otpRequest: OtpRequest){
        viewModelScope.launch {
            registerRepository.sendOtp(otpRequest)
        }
    }




}
