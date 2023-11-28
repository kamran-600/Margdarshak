package com.example.margdarshakendra.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.margdarshakendra.models.LoginRequest
import com.example.margdarshakendra.models.LoginResponse
import com.example.margdarshakendra.repository.LoginRepository
import com.example.margdarshakendra.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(private val loginRepository: LoginRepository) : ViewModel() {

    val loginResponseLiveData: LiveData<NetworkResult<LoginResponse>>
        get() = loginRepository.loginResponseLiveData

    fun loginUser(loginRequest: LoginRequest) {
        viewModelScope.launch {
            loginRepository.loginUser(loginRequest)
        }
    }
}