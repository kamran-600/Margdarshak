package com.margdarshakendra.margdarshak.models

data class GetOrganiserStudyTimeResponse(
    val statusCode: Int,
    val success: Boolean,
    val total_study_time: Double
)