package com.margdarshakendra.margdarshak.models

data class EmailSearchResponse(
    val statusCode: Int,
    val success: Boolean,
    val user: User?
){
    data class User(
        val email: String,
        val name: String,
        val userID: Int
    )
}