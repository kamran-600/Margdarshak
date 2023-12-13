package com.margdarshakendra.margdarshak.models

data class StartAssessmentResponse(
    val result_id: Int,
    val statusCode: Int,
    val success: Boolean,
    val type: String
)