package com.margdarshakendra.margdarshak.models

data class QuestionData(
    val ans_time: String,
    val answer: String?,
    val mcqID: Int,
    val option1: String,
    val option2: String,
    val option3: String,
    val option4: String,
    val question: String,
    val testID: Int
)