package com.margdarshakendra.margdarshak.models

data class InteractiveTeachersResponse(
    val `data`: Data,
    val statusCode: Int,
    val success: Boolean
){
    data class Data(
        val attemp_user_id: Int,
        val courseID: String,
        val status: Boolean,
        val subjectID: String,
        val table_data: List<TableData>
    ){
        data class TableData(
            val activities: List<Any>,
            val class_time: String,
            val contents: List<String>,
            val lesson_id: Int,
            val lesson_name: String,
            val meet_link: String,
            val percentage: Int,
            val practicals: List<Any>,
            val teacher: String,
            val teacher_pic: String?,
            val test_attempt: Boolean,
            val test_counts: Int,
            val videos: List<String>
        )
    }
}