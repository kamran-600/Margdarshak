package com.example.margdarshakendra.models

data class UserRequest(
    val country_code: String,
    val email: String,
    val user_mobile: String,
    val user_name: String,
    val user_type: String,
    val vmobile: String
)