package com.margdarshakendra.margdarshak.models

data class GetOrganiserStudyTimeRequest(
    val lesson_arr: List<Int>,
    val mode: String,
    val weightage: String
)