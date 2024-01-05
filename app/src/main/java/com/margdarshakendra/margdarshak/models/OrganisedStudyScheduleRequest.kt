package com.margdarshakendra.margdarshak.models

data class OrganisedStudyScheduleRequest(
    val course: String,
    val subject: String,
    val lessons: List<String>,
    val language: String,
    val weightage: String,
    val sdate: String,
    val edate: String,
    val holidates: String,
    val scheduleID: String,
    val sdays: List<String>,
    val total_time: String
)