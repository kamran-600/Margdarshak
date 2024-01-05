package com.margdarshakendra.margdarshak.models

data class GetOrganiserCoursesResponse(
    val courses: List<Course>,
    val statusCode: Int,
    val success: Boolean
){
    data class Course(
        val course: String,
        val courseID: Int,
        val coursetype: String
    )
}