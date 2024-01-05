package com.margdarshakendra.margdarshak.models

data class InteractiveSubjectsResponse(
    val statusCode: Int,
    val subjects: List<Subject>,
    val success: Boolean
){
    data class Subject(
        val subject: String,
        val subjectID: Int
    )
}