package com.example.margdarshakendra.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.margdarshakendra.api.AddressSearchApi
import com.example.margdarshakendra.models.CountryRequest
import com.example.margdarshakendra.models.CountryResponse
import com.example.margdarshakendra.models.DistrictRequest
import com.example.margdarshakendra.models.DistrictResponse
import com.example.margdarshakendra.models.PincodeResponse
import com.example.margdarshakendra.models.UserUpdateRequest
import com.example.margdarshakendra.models.UserUpdateResponse
import com.example.margdarshakendra.utils.Constants
import com.example.margdarshakendra.utils.NetworkResult
import org.json.JSONObject
import javax.inject.Inject

class ProfileRepository @Inject constructor(private val addressSearchApi: AddressSearchApi) {

    private val _districtResponseLiveData = MutableLiveData<NetworkResult<DistrictResponse>>()
    val districtResponseLiveData: LiveData<NetworkResult<DistrictResponse>>
        get() = _districtResponseLiveData

    private val _countryResponseLiveData = MutableLiveData<NetworkResult<CountryResponse>>()
    val countryResponseLiveData: LiveData<NetworkResult<CountryResponse>>
        get() = _countryResponseLiveData

    private val _pincodeResponseLiveData = MutableLiveData<NetworkResult<PincodeResponse>>()
    val pincodeResponseLiveData: LiveData<NetworkResult<PincodeResponse>>
        get() = _pincodeResponseLiveData

    private val _userUpdateResponseLiveData = MutableLiveData<NetworkResult<UserUpdateResponse>>()
    val userUpdateResponseLiveData: LiveData<NetworkResult<UserUpdateResponse>>
        get() = _userUpdateResponseLiveData


    suspend fun getDistrict(districtRequest: DistrictRequest) {
        try {
            val districtListResponse = addressSearchApi.getDistrict(districtRequest)

            if (districtListResponse.isSuccessful && districtListResponse.body() != null) {
                _districtResponseLiveData.postValue(NetworkResult.Success(districtListResponse.body()!!))
            } else if (districtListResponse.errorBody() != null) {
                val jsonLoginError =
                    JSONObject(districtListResponse.errorBody()!!.charStream().readText())
                _districtResponseLiveData.postValue(NetworkResult.Error(jsonLoginError.getString("message")))
            }
        } catch (e: Exception) {
            Log.d(Constants.TAG, e.message.toString())
            _districtResponseLiveData.postValue(NetworkResult.Error(e.message))
        }
    }

    suspend fun getCountry(countryRequest: CountryRequest) {
        try {
            val countryListResponse = addressSearchApi.getCountry(countryRequest)

            if (countryListResponse.isSuccessful && countryListResponse.body() != null) {
                _countryResponseLiveData.postValue(NetworkResult.Success(countryListResponse.body()!!))
            } else if (countryListResponse.errorBody() != null) {
                val jsonLoginError =
                    JSONObject(countryListResponse.errorBody()!!.charStream().readText())
                _countryResponseLiveData.postValue(NetworkResult.Error(jsonLoginError.getString("message")))
            }
        } catch (e: Exception) {
            Log.d(Constants.TAG, e.message.toString())
            _districtResponseLiveData.postValue(NetworkResult.Error(e.message))
        }
    }

    suspend fun getPincode(q : String) {
        try {
            val pincodeListResponse = addressSearchApi.getPincode(q)

            if (pincodeListResponse.isSuccessful && pincodeListResponse.body() != null) {
                _pincodeResponseLiveData.postValue(NetworkResult.Success(pincodeListResponse.body()!!))
            } else if (pincodeListResponse.errorBody() != null) {
                val jsonLoginError =
                    JSONObject(pincodeListResponse.errorBody()!!.charStream().readText())
                _pincodeResponseLiveData.postValue(NetworkResult.Error(jsonLoginError.getString("message")))
            }
        } catch (e: Exception) {
            Log.d(Constants.TAG, e.message.toString())
            _pincodeResponseLiveData.postValue(NetworkResult.Error(e.message))
        }
    }

    suspend fun userUpdateDetails(userUpdateRequest: UserUpdateRequest) {
        try {
            val userUpdateResponse = addressSearchApi.updateUserDetails(userUpdateRequest)

            if (userUpdateResponse.isSuccessful && userUpdateResponse.body() != null) {
                _userUpdateResponseLiveData.postValue(NetworkResult.Success(userUpdateResponse.body()!!))
            } else if (userUpdateResponse.errorBody() != null) {
                val jsonLoginError =
                    JSONObject(userUpdateResponse.errorBody()!!.charStream().readText())
                _userUpdateResponseLiveData.postValue(NetworkResult.Error(jsonLoginError.getString("message")))
            }
        } catch (e: Exception) {
            Log.d(Constants.TAG, "exception "+e.message.toString())
            _userUpdateResponseLiveData.postValue(NetworkResult.Error(e.message))
        }
    }








}