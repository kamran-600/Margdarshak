package com.example.margdarshakendra.models

data class PincodeResponse(
    val pincode: List<Pincode>,
    val statusCode: Int
) {
    data class Pincode(
        val associate: String,
        val pinID: Int,
        val pincode: String,
        val postoffice: String,
        val state_code: String
    )
}