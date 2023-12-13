package com.margdarshakendra.margdarshak.models

data class QuestionsResponse(
    val questions: List<Question>,
    val statusCode: Int,
    val success: Boolean
){
    data class Question(
        val aptiquest: String,
        val aptiquestID: Int,
        val ans : String?
    )

}