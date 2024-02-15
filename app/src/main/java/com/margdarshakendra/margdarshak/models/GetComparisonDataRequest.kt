package com.margdarshakendra.margdarshak.models

data class GetComparisonDataRequest(
    val mode: String,
    val competitors: String,
    val course: String,
    val subject: String,
    val lesson: String? = null
)