package com.margdarshakendra.margdarshak.models

data class InteractiveTestDetailsResponse(
    val statusCode: Int,
    val success: Boolean,
    val test_details: List<TestDetail>
)