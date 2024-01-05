package com.margdarshakendra.margdarshak.models

data class SaveFcmTokenResponse(
    val message: String,
    val statusCode: Int,
    val success: Boolean,
    val token: String
)