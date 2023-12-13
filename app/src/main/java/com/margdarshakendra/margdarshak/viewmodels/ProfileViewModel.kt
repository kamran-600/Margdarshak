package com.margdarshakendra.margdarshak.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.margdarshakendra.margdarshak.models.CountryRequest
import com.margdarshakendra.margdarshak.models.CountryResponse
import com.margdarshakendra.margdarshak.models.DistrictRequest
import com.margdarshakendra.margdarshak.models.DistrictResponse
import com.margdarshakendra.margdarshak.models.PincodeResponse
import com.margdarshakendra.margdarshak.models.UserUpdateRequest
import com.margdarshakendra.margdarshak.models.UserUpdateResponse
import com.margdarshakendra.margdarshak.repository.ProfileRepository
import com.margdarshakendra.margdarshak.utils.NetworkResult
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