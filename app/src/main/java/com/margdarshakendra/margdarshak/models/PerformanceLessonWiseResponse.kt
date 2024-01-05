package com.margdarshakendra.margdarshak.models

data class PerformanceLessonWiseResponse(
    val performance_lesson_wise: List<PerformanceLessonWise>,
    val statusCode: Int,
    val success: Boolean
){
    data class PerformanceLessonWise(
        val lesson: String,
        val lessonID: Int,
        val marks: String?
    )
}