package com.margdarshakendra.margdarshak.models

data class SaveSkillTestAnswerRequest(
    val ans: String?,
    val mcqID: String,
    val result_id: String,
    val submitType: String,
    val testID: String
)