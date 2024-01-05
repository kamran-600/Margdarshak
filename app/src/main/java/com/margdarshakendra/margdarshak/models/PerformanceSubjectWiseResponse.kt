package com.margdarshakendra.margdarshak.models

data class PerformanceSubjectWiseResponse(
    val performance_subject_wise: List<PerformanceSubjectWise>,
    val statusCode: Int,
    val success: Boolean
){
    data class PerformanceSubjectWise(
    val marks: String?,
    val subject: String,
    val subjectID: Int
    )

}