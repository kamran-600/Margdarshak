package com.margdarshakendra.margdarshak.models

data class McqResultsResponse(
    val mcq_results: List<McqResult>,
    val statusCode: Int,
    val success: Boolean
){
    data class McqResult(
        val ans_correct: String,
        val edate: String,
        val finished: String,
        val lessonID: String,
        val loginIP: String,
        val marks: String?,
        val ques_attempt: String?,
        val ques_total: String,
        val resultID: Int,
        val time_taken: String,
        val userID: String
    )
}