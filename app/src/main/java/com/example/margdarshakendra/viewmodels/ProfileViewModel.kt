package com.example.margdarshakendra.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.margdarshakendra.models.CountryRequest
import com.example.margdarshakendra.models.CountryResponse
import com.example.margdarshakendra.models.DistrictRequest
import com.example.margdarshakendra.models.DistrictResponse
import com.example.margdarshakendra.models.PincodeResponse
import com.example.margdarshakendra.models.UserUpdateRequest
import com.example.margdarshakendra.models.UserUpdateResponse
import com.example.margdarshakendra.repository.ProfileRepository
import com.example.margdarshakendra.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val profileRepository: ProfileRepository) :
    ViewModel() {

    val districtResponseLiveData: LiveData<NetworkResult<DistrictResponse>>
        get() = profileRepository.districtResponseLiveData

    val countryResponseLiveData: LiveData<NetworkResult<CountryResponse>>
        get() = profileRepository.countryResponseLiveData

    val pincodeResponseLiveData: LiveData<NetworkResult<PincodeResponse>>
        get() = profileRepository.pincodeResponseLiveData

    val userUpdateResponseLiveData: LiveData<NetworkResult<UserUpdateResponse>>
        get() = profileRepository.userUpdateResponseLiveData

    fun getDistrict(districtRequest: DistrictRequest) {
        viewModelScope.launch {
            profileRepository.getDistrict(districtRequest)
        }
    }

    fun getCountry(countryRequest: CountryRequest) {
        viewModelScope.launch {
            profileRepository.getCountry(countryRequest)
        }
    }

    fun getPincode(q : String) {
        viewModelScope.launch {
            profileRepository.getPincode(q)
        }
    }

    fun updateUserDetails(userUpdateRequest: UserUpdateRequest){
        viewModelScope.launch {
            profileRepository.userUpdateDetails(userUpdateRequest)
        }
    }

}