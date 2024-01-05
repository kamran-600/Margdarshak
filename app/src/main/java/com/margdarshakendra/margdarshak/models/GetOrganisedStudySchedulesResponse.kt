package com.margdarshakendra.margdarshak.models

data class GetOrganisedStudySchedulesResponse(
    val avavilable_schedule_id: Any?,
    val statusCode: Int,
    val success: Boolean,
    val table_data: List<TableData>
){
    data class TableData(
        val course: String,
        val date_finish: String,
        val date_leave: String,
        val date_start: String,
        val lesson_names: String,
        val scheduleID: Int,
        val study_days: String,
        val study_time: String,
        val subject: String,
        val weightage: String
    )
}