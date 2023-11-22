package com.example.margdarshakendra.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.margdarshakendra.models.OtpRequest
import com.example.margdarshakendra.models.OtpResponse
import com.example.margdarshakendra.models.UserRequest
import com.example.margdarshakendra.models.UserResponse
import com.example.margdarshakendra.repository.UserRepository
import com.example.margdarshakendra.utils.NetworkResult
import kotlinx.coroutines.launch


class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {

    val userResponseLiveData: LiveData<NetworkResult<UserResponse>>
        get() = userRepository.userResponseLiveData

    val otpResponseLiveData: LiveData<NetworkResult<OtpResponse>>
        get() = userRepository.otpResponseLiveData


    fun registerUser(email: String, userName: String, userType: String, userMobile: String, countryCode: String, vMobile: String, userRequest: UserRequest) {
        viewModelScope.launch {
                userRepository.registerUser(email, userName, userType, userMobile, countryCode, vMobile, userRequest)
        }
    }

    fun sendOtp(otpRequest: OtpRequest){
        viewModelScope.launch {
            userRepository.sendOtp(otpRequest)
        }
    }


}
