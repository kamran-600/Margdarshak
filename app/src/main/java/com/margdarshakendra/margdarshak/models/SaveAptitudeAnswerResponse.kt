package com.margdarshakendra.margdarshak.models

data class SaveAptitudeAnswerResponse(
    val completed: Boolean,
    val message: String,
    val page_no: Int,
    val statusCode: Int,
    val success: Boolean
)