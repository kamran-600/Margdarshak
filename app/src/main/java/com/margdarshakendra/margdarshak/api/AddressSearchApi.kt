package com.margdarshakendra.margdarshak.api

import com.margdarshakendra.margdarshak.models.CountryRequest
import com.margdarshakendra.margdarshak.models.CountryResponse
import com.margdarshakendra.margdarshak.models.DistrictRequest
import com.margdarshakendra.margdarshak.models.DistrictResponse
import com.margdarshakendra.margdarshak.models.PincodeResponse
import com.margdarshakendra.margdarshak.models.UserUpdateRequest
import com.margdarshakendra.margdarshak.models.UserUpdateResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface AddressSearchApi {

    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("get-mast-data")
    suspend fun getDistrict(@Body districtRequest: DistrictRequest): Response<DistrictResponse>

    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("get-mast-data")
    suspend fun getCountry(@Body countryRequest: CountryRequest): Response<CountryResponse>

    @Headers(
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @GET("search-pincode")
    suspend fun getPincode(@Query("q") q: String): Response<PincodeResponse>


    /*@Headers(
        "Content-Type: multipart/form-data",
        "access-key: 5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept: application/json"
    )
    @GET("user-update")
    suspend fun getUserDetails(): Response<UserDetailResponse>
*/
    @Headers(
        "Content-Type:application/json",
        "access-key: 5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept: application/json"
    )
    @POST("user-update")
    suspend fun updateUserDetails(@Body userUpdateRequest: UserUpdateRequest): Response<UserUpdateResponse>



}