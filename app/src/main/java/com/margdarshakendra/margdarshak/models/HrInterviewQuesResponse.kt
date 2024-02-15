package com.margdarshakendra.margdarshak.models

data class HrInterviewQuesResponse(
    val questions: List<Question>,
    val resid: String,
    val statusCode: Int,
    val success: Boolean
){
    data class Question(
        val hrquestID: Int,
        val question: String,
        val rank: Int
    )
}