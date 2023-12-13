package com.margdarshakendra.margdarshak.models

data class SaveAttitudeQuestionsResponse(
    val assessment: Boolean,
    val completed: Boolean,
    val page_no: String,
    val rating: Boolean,
    val result_id: String,
    val statusCode: Int,
    val success: Boolean
)