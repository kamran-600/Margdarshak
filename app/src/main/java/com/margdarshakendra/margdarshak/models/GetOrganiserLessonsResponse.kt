package com.margdarshakendra.margdarshak.models

data class GetOrganiserLessonsResponse(
    val lessons: List<Lesson>,
    val statusCode: Int,
    val success: Boolean
){
    data class Lesson(
        val lesson: String,
        val lessonID: Int,
        val studyID: String
    )
}