package com.margdarshakendra.margdarshak.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.margdarshakendra.margdarshak.models.LoginRequest
import com.margdarshakendra.margdarshak.models.LoginResponse
import com.margdarshakendra.margdarshak.repository.LoginRepository
import com.margdarshakendra.margdarshak.utils.NetworkResult
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