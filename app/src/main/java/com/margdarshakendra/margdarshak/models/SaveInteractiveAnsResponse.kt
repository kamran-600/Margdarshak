package com.margdarshakendra.margdarshak.models

data class SaveInteractiveAnsResponse(
    val ans: String,
    val statusCode: Int,
    val success: Boolean,
    val message : String?,
    val testID: Int,
    val timeremainigSecs: Int
)