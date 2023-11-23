package com.example.margdarshakendra.models

data class LoginResponse(
    val login_id: Int,
    val message: String,
    val statusCode: Int,
    val success: Boolean,
    val token: String
)