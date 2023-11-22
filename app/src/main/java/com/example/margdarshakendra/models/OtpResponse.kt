package com.example.margdarshakendra.models

data class OtpResponse(
    val otp: Int,
    val statusCode: Int,
    val success: Boolean
)