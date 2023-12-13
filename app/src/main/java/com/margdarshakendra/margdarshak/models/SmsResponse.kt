package com.margdarshakendra.margdarshak.models

data class SmsResponse(
    val message: String,
    val statusCode: Int,
    val success: Boolean
)