package com.margdarshakendra.margdarshak.models

data class LoginResponse(
    val login_id: Int,
    val message: String,
    val statusCode: Int,
    val success: Boolean,
    val token: String,
    val email: String,
    val mobile: String,
    val profile_updated: Boolean,
    val usertype: String,
    val name: String

)