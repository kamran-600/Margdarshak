package com.margdarshakendra.margdarshak.api

import com.margdarshakendra.margdarshak.models.ForgetPasswordRequest
import com.margdarshakendra.margdarshak.models.LoginRequest
import com.margdarshakendra.margdarshak.models.LoginResponse
import com.margdarshakendra.margdarshak.models.OtpRequest
import com.margdarshakendra.margdarshak.models.OtpResponse
import com.margdarshakendra.margdarshak.models.RegisterRequest
import com.margdarshakendra.margdarshak.models.RegisterResponse
import com.margdarshakendra.margdarshak.models.SmsResponse
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

    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("forget-pass")
    suspend fun forgetPasswordRequest(
        @Body forgetPasswordRequest: ForgetPasswordRequest
    ): Response<SmsResponse>


}