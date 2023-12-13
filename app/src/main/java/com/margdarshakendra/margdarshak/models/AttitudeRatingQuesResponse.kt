package com.margdarshakendra.margdarshak.models

data class AttitudeRatingQuesResponse(
    val questions: List<Question>,
    val statusCode: Int,
    val success: Boolean
){
    data class Question(
        val attiquest: String,
        val attiquestID: Int
    )
}