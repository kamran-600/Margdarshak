package com.margdarshakendra.margdarshak.models

data class RegisterResponse(
    val message: String,
    val statusCode: Int,
    val success: Boolean
)