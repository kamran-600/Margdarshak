package com.margdarshakendra.margdarshak.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.margdarshakendra.margdarshak.api.UserApi
import com.margdarshakendra.margdarshak.models.LoginRequest
import com.margdarshakendra.margdarshak.models.LoginResponse
import com.margdarshakendra.margdarshak.utils.Constants
import com.margdarshakendra.margdarshak.utils.NetworkResult
import org.json.JSONObject
import javax.inject.Inject

class LoginRepository @Inject constructor(private val userApi: UserApi) {

    private val _loginResponseLiveData = MutableLiveData<NetworkResult<LoginResponse>>()
    val loginResponseLiveData: LiveData<NetworkResult<LoginResponse>>
        get() = _loginResponseLiveData

    suspend fun loginUser(loginRequest: LoginRequest) {
        _loginResponseLiveData.postValue(NetworkResult.Loading())
        try {
            val loginResponse = userApi.loginUser(loginRequest)

            if (loginResponse.isSuccessful && loginResponse.body() != null) {
                _loginResponseLiveData.postValue(NetworkResult.Success(loginResponse.body()!!))
            } else if (loginResponse.errorBody() != null) {
                val jsonLoginError = JSONObject(loginResponse.errorBody()!!.charStream().readText())
                _loginResponseLiveData.postValue(NetworkResult.Error(jsonLoginError.getString("message")))
            }
        } catch (e: Exception) {
            Log.d(Constants.TAG, e.message.toString())
            _loginResponseLiveData.postValue(NetworkResult.Error(e.message))
        }
    }

}