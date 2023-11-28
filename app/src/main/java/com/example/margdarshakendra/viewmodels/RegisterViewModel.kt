package com.example.margdarshakendra.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.margdarshakendra.models.OtpRequest
import com.example.margdarshakendra.models.OtpResponse
import com.example.margdarshakendra.models.RegisterRequest
import com.example.margdarshakendra.models.RegisterResponse
import com.example.margdarshakendra.repository.RegisterRepository
import com.example.margdarshakendra.utils.NetworkResult
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
