package com.margdarshakendra.margdarshak.models

data class CalenderSchedulesResponse(
    val lessons: List<Lesson>,
    val statusCode: Int,
    val success: Boolean
){
    data class Lesson(
        val date_finish: String,
        val date_start: String,
        val study_days: String,
        val subject: String
    )
}