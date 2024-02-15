package com.margdarshakendra.margdarshak.models

data class ProgressMarksAndTimeResponse(
    val `data`: Data?,
    val statusCode: Int,
    val success: Boolean
){
    data class Data(
        val `data`: DataX?
    ){
        data class DataX(
            val date_finish: String?,
            val date_start: String?,
            val marks: String?,
            val subject: String,
            val subjectID: Int,
            val t_marks: String?,
            val t_pass: String
        )
    }
}