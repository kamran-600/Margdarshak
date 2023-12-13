package com.margdarshakendra.margdarshak.models

data class ClientDataResponse(
    val `data`: List<Data>,
    val statusCode: Int,
    val success: Boolean
) {
    data class Data(
        val userID: Int,
        val name: String,
        val pic: String?,
        val usertype: String,
        val `class`: String?,
        val inst: String?,
        val qualification : String?,
        val specialise : String?,
        val pincode: String?,
        val district: String?,
        val state: String?,
        val position: String?,
        val email_verified: String,
        val mobile_verified: String
    )
}