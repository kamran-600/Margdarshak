package com.margdarshakendra.margdarshak.models

data class SkillTestQuestionResponse(
    val `data`: List<Data>,
    val statusCode: Int,
    val success: Boolean
){
    data class Data(
        val answer: String,
        val mcqID: Int,
        val option1: String,
        val option2: String,
        val option3: String,
        val option4: String,
        val question: String,
        val testID: Int
    )
}