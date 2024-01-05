package com.margdarshakendra.margdarshak.models

data class UpdateTimerResponse(
    val statusCode: Int,
    val success: Boolean,
    val timerValue: String
)