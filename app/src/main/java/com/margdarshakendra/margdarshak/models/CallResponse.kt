package com.margdarshakendra.margdarshak.models

data class CallResponse(
    val `data`: Data,
    val statusCode: Int,
    val success: Boolean
){
    data class Data(
        val message: String,
        val success: Boolean
    )
}