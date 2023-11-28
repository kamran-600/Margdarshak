package com.example.margdarshakendra.api

import com.example.margdarshakendra.models.DistrictRequest
import com.example.margdarshakendra.models.DistrictResponse
import com.example.margdarshakendra.models.LoginRequest
import com.example.margdarshakendra.models.LoginResponse
import com.example.margdarshakendra.models.OtpRequest
import com.example.margdarshakendra.models.OtpResponse
import com.example.margdarshakendra.models.RegisterRequest
import com.example.margdarshakendra.models.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface UserApi {

    @Headers(
        "Content-Type: application/json",
        "access-key: 5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept: application/json"
    )
    @POST("register")
    suspend fun registerUser(
        @Body registerRequest: RegisterRequest
    ): Response<RegisterResponse>


    @Headers(
        "Content-Type: application/json",
        "access-key: 5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept: application/json"
    )
    @POST("register/send-otp")
    suspend fun sendOTP(
        @Body otpRequest: OtpRequest
    ): Response<OtpResponse>


    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("login")
    suspend fun loginUser(
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>


}