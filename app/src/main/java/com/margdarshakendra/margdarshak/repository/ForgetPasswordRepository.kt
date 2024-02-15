package com.margdarshakendra.margdarshak.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.margdarshakendra.margdarshak.api.UserApi
import com.margdarshakendra.margdarshak.models.ForgetPasswordRequest
import com.margdarshakendra.margdarshak.models.SmsResponse
import com.margdarshakendra.margdarshak.utils.Constants
import com.margdarshakendra.margdarshak.utils.NetworkResult
import org.json.JSONObject
import javax.inject.Inject

class ForgetPasswordRepository @Inject constructor(private val userApi: UserApi) {

    private val _forgetPasswordResponseLiveData = MutableLiveData<NetworkResult<SmsResponse>>()

    val forgetPasswordResponseLiveData: LiveData<NetworkResult<SmsResponse>>
        get() = _forgetPasswordResponseLiveData


    suspend fun forgetPasswordRequest(forgetPasswordRequest: ForgetPasswordRequest) {

        _forgetPasswordResponseLiveData.postValue(NetworkResult.Loading())
        try {
            val forgetPasswordResponse = userApi.forgetPasswordRequest(forgetPasswordRequest)

            if (forgetPasswordResponse.isSuccessful && forgetPasswordResponse.body() != null) {
                _forgetPasswordResponseLiveData.postValue(
                    NetworkResult.Success(
                        forgetPasswordResponse.body()!!
                    )
                )
            } else if (forgetPasswordResponse.errorBody() != null) {
                val jsonError =
                    JSONObject(forgetPasswordResponse.errorBody()!!.charStream().readText())
                _forgetPasswordResponseLiveData.postValue(
                    NetworkResult.Error(
                        jsonError.getString(
                            "message"
                        )
                    )
                )

            }
        } catch (e: Exception) {
            Log.d(Constants.TAG, e.message.toString())
            _forgetPasswordResponseLiveData.postValue(NetworkResult.Error(e.message))
        }
    }


}