package com.example.margdarshakendra.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.margdarshakendra.api.UserApi
import com.example.margdarshakendra.models.OtpRequest
import com.example.margdarshakendra.models.OtpResponse
import com.example.margdarshakendra.models.UserRequest
import com.example.margdarshakendra.models.UserResponse
import com.example.margdarshakendra.utils.Constants.TAG
import com.example.margdarshakendra.utils.NetworkResult
import org.json.JSONObject


class UserRepository(private val userApi: UserApi) {

    private val _userResponseLiveData = MutableLiveData<NetworkResult<UserResponse>>()
    val userResponseLiveData: LiveData<NetworkResult<UserResponse>>
        get() = _userResponseLiveData


    private val _otpResponseLiveData = MutableLiveData<NetworkResult<OtpResponse>>()
    val otpResponseLiveData: LiveData<NetworkResult<OtpResponse>>
        get() = _otpResponseLiveData



    suspend fun registerUser(
        email: String,
        userName: String,
        userType: String,
        userMobile: String,
        countryCode: String,
        vMobile: String,
        userRequest: UserRequest
    ) {

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
                    UserResponse(
                        jsonError.getString("message"),
                        jsonError.getInt("statusCode"),
                        jsonError.getBoolean("success")
                    )
                )
            }

        } catch (e: Exception) {
            Log.d(TAG, "exception" + e.message.toString())
            _userResponseLiveData.postValue(UserResponse(e.message.toString(), -1, false))
        }
*/

        try {
            val response = userApi.registerUser(email,
                userName,
                userType,
                userMobile,
                countryCode,
                vMobile,
                userRequest)

            if(response.isSuccessful && response.body() != null){
                _userResponseLiveData.postValue(NetworkResult.Success(response.body()!!))
            }
            else if(response.errorBody() != null){
                val jsonOtpError = JSONObject(response.errorBody()!!.charStream().readText())
                _userResponseLiveData.postValue(NetworkResult.Error(jsonOtpError.getString("message")))
            }
        } catch ( e : Exception){
            Log.d(TAG, e.message.toString())
            _userResponseLiveData.postValue(NetworkResult.Error(e.message))
        }
    }


    suspend fun sendOtp(otpRequest: OtpRequest) {


            try {
                val otpResponse = userApi.sendOTP(otpRequest)

                if(otpResponse.isSuccessful && otpResponse.body() != null){
                    _otpResponseLiveData.postValue(NetworkResult.Success(otpResponse.body()!!))
                }
                else if(otpResponse.errorBody() != null){
                    val jsonOtpError = JSONObject(otpResponse.errorBody()!!.charStream().readText())
                    _otpResponseLiveData.postValue(NetworkResult.Error(jsonOtpError.getString("message")))
                }
            } catch ( e : Exception){
                Log.d(TAG, e.message.toString())
                _otpResponseLiveData.postValue(NetworkResult.Error(e.message))
            }






    }
}