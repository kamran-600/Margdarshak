package com.margdarshakendra.margdarshak.models

data class GetOrganiserLessonsRequest(
    val course: String,
    val mode: String,
    val subject: String
)