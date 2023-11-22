package com.example.margdarshakendra.api

import com.example.margdarshakendra.models.OtpRequest
import com.example.margdarshakendra.models.OtpResponse
import com.example.margdarshakendra.models.UserRequest
import com.example.margdarshakendra.models.UserResponse
import com.example.margdarshakendra.utils.NetworkResult
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface UserApi {

    @Headers(
        "Content-Type: application/json",
        "access-key: 5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept: application/json"
    )
    @POST("register")
    suspend fun registerUser(
        @Query("email") email: String,
        @Query("user_name") userName: String,
        @Query("user_type") userType: String,
        @Query("user_mobile") userMobile: String,
        @Query("country_code") countryCode: String,
        @Query("vmobile") vMobile: String,
        @Body userRequest: UserRequest
    ): Response<UserResponse>


    @Headers(
        "Content-Type: application/json",
        "access-key: 5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept: application/json"
    )
    @POST("register/send-otp")
    suspend fun sendOTP(
        @Body otpRequest: OtpRequest
    ): Response<OtpResponse>
}