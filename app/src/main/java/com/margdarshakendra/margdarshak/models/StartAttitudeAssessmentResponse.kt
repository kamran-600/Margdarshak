package com.margdarshakendra.margdarshak.models

data class StartAttitudeAssessmentResponse(
    val assessment: Boolean,
    val completed: Boolean,
    val rating: Boolean,
    val result_id: Int,
    val statusCode: Int,
    val success: Boolean
)