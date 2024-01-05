package com.margdarshakendra.margdarshak.models

data class GetInteractiveQuestionRequest(
    val question_data: List<QuestionData>,
    val statusCode: Int,
    val success: Boolean
)