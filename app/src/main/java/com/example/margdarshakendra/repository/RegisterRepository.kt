package com.example.margdarshakendra.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.margdarshakendra.api.UserApi
import com.example.margdarshakendra.models.OtpRequest
import com.example.margdarshakendra.models.OtpResponse
import com.example.margdarshakendra.models.RegisterRequest
import com.example.margdarshakendra.models.RegisterResponse
import com.example.margdarshakendra.utils.Constants.TAG
import com.example.margdarshakendra.utils.NetworkResult
import org.json.JSONObject
import javax.inject.Inject


class RegisterRepository @Inject constructor(private val userApi: UserApi) {

    private val _registerResponseLiveData = MutableLiveData<NetworkResult<RegisterResponse>>()
    val registerResponseLiveData: LiveData<NetworkResult<RegisterResponse>>
        get() = _registerResponseLiveData


    private val _otpResponseLiveData = MutableLiveData<NetworkResult<OtpResponse>>()
    val otpResponseLiveData: LiveData<NetworkResult<OtpResponse>>
        get() = _otpResponseLiveData




    suspend fun registerUser(registerRequest: RegisterRequest) {

        /*try {
            val response = userApi.registerUser(
                email,
                userName,
                userType,
                userMobile,
                countryCode,
                vMobile,
                userRequest
            )

            val jsonError = response.errorBody()?.string()?.let { JSONObject(it) }

            if (response.body() != null) {
                _userResponseLiveData.postValue(response.body()!!)
            } else if (jsonError != null) {
                _userResponseLiveData.postValue(
                    RegisterResponse(
                        jsonError.getString("message"),
                        jsonError.getInt("statusCode"),
                        jsonError.getBoolean("success")
                    )
                )
            }

        } catch (e: Exception) {
            Log.d(TAG, "exception" + e.message.toString())
            _userResponseLiveData.postValue(RegisterResponse(e.message.toString(), -1, false))
        }
*/

        try {
            val response = userApi.registerUser(
                registerRequest
            )

            if (response.isSuccessful && response.body() != null) {
                _registerResponseLiveData.postValue(NetworkResult.Success(response.body()!!))
            } else if (response.errorBody() != null) {
                val jsonOtpError = JSONObject(response.errorBody()!!.charStream().readText())
                _registerResponseLiveData.postValue(NetworkResult.Error(jsonOtpError.getString("message")))
            }
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
            _registerResponseLiveData.postValue(NetworkResult.Error(e.message))
        }
    }


    suspend fun sendOtp(otpRequest: OtpRequest) {


        try {
            val otpResponse = userApi.sendOTP(otpRequest)

            if (otpResponse.isSuccessful && otpResponse.body() != null) {
                _otpResponseLiveData.postValue(NetworkResult.Success(otpResponse.body()!!))
            } else if (otpResponse.errorBody() != null) {
                val jsonOtpError = JSONObject(otpResponse.errorBody()!!.charStream().readText())
                _otpResponseLiveData.postValue(NetworkResult.Error(jsonOtpError.getString("message")))
            }
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
            _otpResponseLiveData.postValue(NetworkResult.Error(e.message))
        }


    }


}