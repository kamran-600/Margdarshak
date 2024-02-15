package com.margdarshakendra.margdarshak.models

data class UpdateCommunicationTimerResponse(
    val message: Message,
    val statusCode: Int,
    val success: Boolean
){
    data class Message(
        val time: Int
    )
}