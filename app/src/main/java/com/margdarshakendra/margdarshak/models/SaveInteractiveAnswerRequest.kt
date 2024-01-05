package com.margdarshakendra.margdarshak.models

data class SaveInteractiveAnswerRequest(
    val ans: String,
    val mcqID: Int,
    val resultID: Int,
    val submitType: String,
    val testID: Int,
    val timeremainigSecs: Int
)