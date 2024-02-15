package com.margdarshakendra.margdarshak.models

data class HrInterviewQuesUtilResponse(
    val `data`: Data,
    val statusCode: Int
){
    data class Data(
        val status: Boolean
    )
}