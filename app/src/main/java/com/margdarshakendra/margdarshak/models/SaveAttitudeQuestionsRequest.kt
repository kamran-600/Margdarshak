package com.margdarshakendra.margdarshak.models

data class SaveAttitudeQuestionsRequest(
    val result_id: String,
    val page_no: String,
    val que: List<String>
)