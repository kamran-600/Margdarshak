package com.margdarshakendra.margdarshak.models

data class ScheduleNotificationRequest(
    val date: String,
    val reminder: Int,
    val reminder_count: Int,
    val task: String,
    val task_name: String,
    val time: String
)