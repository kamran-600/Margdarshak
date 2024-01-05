package com.margdarshakendra.margdarshak.models

data class EditOrganisedStudyScheduleResponse(
    val editable_data: EditableData,
    val statusCode: Int,
    val success: Boolean
){
    data class EditableData(
        val LessonID: String,
        val courseID: String,
        val date_finish: String,
        val date_leave: String?,
        val date_start: String,
        val edate: String,
        val euser: String,
        val itemID: String,
        val medium: String,
        val scheduleID: Int,
        val studyID: String,
        val study_days: String,
        val study_time: String,
        val subjectID: String,
        val userID: String,
        val weightage: String
    )
}