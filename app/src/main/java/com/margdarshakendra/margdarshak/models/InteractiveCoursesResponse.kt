package com.margdarshakendra.margdarshak.models

data class InteractiveCoursesResponse(
    val interactiveCourses: List<InteractiveCourse>,
    val statusCode: Int,
    val success: Boolean
){
    data class InteractiveCourse(
        val course: String,
        val courseID: Int
    )
}