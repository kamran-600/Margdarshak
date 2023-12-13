package com.margdarshakendra.margdarshak.models

data class AttitudeQuestionsResponse(
    val `data`: List<Data>,
    val page_no: String,
    val statusCode: Int,
    val success: Boolean
){
    data class Data(
        val attiquest: String,
        val attiquestID: Int,
        var isSelected: Boolean = false
    )
}