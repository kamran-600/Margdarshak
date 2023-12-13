package com.margdarshakendra.margdarshak.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.margdarshakendra.margdarshak.api.AddressSearchApi
import com.margdarshakendra.margdarshak.models.CountryRequest
import com.margdarshakendra.margdarshak.models.CountryResponse
import com.margdarshakendra.margdarshak.models.DistrictRequest
import com.margdarshakendra.margdarshak.models.DistrictResponse
import com.margdarshakendra.margdarshak.models.PincodeResponse
import com.margdarshakendra.margdarshak.models.UserUpdateRequest
import com.margdarshakendra.margdarshak.models.UserUpdateResponse
import com.margdarshakendra.margdarshak.utils.Constants.TAG
import com.margdarshakendra.margdarshak.utils.NetworkResult
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
            Log.d(TAG, e.message.toString())
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
            Log.d(TAG, e.message.toString())
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
            Log.d(TAG, "pincode"+e.message.toString())
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
            Log.d(TAG, "exception "+e.message.toString())
            _userUpdateResponseLiveData.postValue(NetworkResult.Error(e.message))
        }
    }








}