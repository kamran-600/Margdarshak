package com.example.margdarshakendra.models

data class UserUpdateResponse(
    val email_verified: String,
    val message: String,
    val profile_updated: Boolean,
    val statusCode: Int,
    val success: Boolean
)