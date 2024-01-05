package com.margdarshakendra.margdarshak.models

data class StartInteractiveTestResponse(
    val resultID: Int,
    val statusCode: Int,
    val success: Boolean,
    val timeAlloted: Int
)