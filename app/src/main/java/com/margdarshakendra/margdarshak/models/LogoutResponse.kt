package com.margdarshakendra.margdarshak.models

data class LogoutResponse(
    val message: String,
    val statusCode: Int,
    val success: Boolean
)