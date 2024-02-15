package com.margdarshakendra.margdarshak.models

data class HrIntervieweeQuestionResponse(
    val `data`: Data,
    val statusCode: Int,
    val success: Boolean
){
    data class Data(
        val message: String
    )
}