package com.margdarshakendra.margdarshak.models

data class SaveAttitudeRatingQuesResponse(
    val assessment: Boolean,
    val completed: Boolean,
    val rating: Boolean,
    val reference: Boolean,
    val result_id: String,
    val statusCode: Int,
    val success: Boolean
)