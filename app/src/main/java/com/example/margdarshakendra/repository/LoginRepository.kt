package com.example.margdarshakendra.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.margdarshakendra.api.UserApi
import com.example.margdarshakendra.models.LoginRequest
import com.example.margdarshakendra.models.LoginResponse
import com.example.margdarshakendra.utils.Constants
import com.example.margdarshakendra.utils.NetworkResult
import org.json.JSONObject

class LoginRepository(private val userApi: UserApi) {

    private val _loginResponseLiveData = MutableLiveData<NetworkResult<LoginResponse>>()
    val loginResponseLiveData: LiveData<NetworkResult<LoginResponse>>
        get() = _loginResponseLiveData

    suspend fun loginUser(loginRequest: LoginRequest) {
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