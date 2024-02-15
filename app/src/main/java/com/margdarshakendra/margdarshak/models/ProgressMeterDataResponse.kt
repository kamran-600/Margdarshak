package com.margdarshakendra.margdarshak.models

data class ProgressMeterDataResponse(
    val `data`: Data,
    val statusCode: Int,
    val success: Boolean
){
    data class Data(
        val `data`: DataX,
        val result_subject_wise: List<ResultSubjectWise>
    ){
        data class DataX(
            val marks: String?,
            val t_marks: String?,
            val t_pass: String
        )
        data class ResultSubjectWise(
            val marks: String?,
            val subject: String,
            val subjectID: Int,
            val t_marks: String?
        )
    }


}