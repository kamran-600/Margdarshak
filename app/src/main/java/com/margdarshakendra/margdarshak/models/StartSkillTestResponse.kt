package com.margdarshakendra.margdarshak.models

data class StartSkillTestResponse(
    val mcq_time: String,
    val result_id: Int,
    val statusCode: Int,
    val ques_count: String,
    val success: Boolean
)