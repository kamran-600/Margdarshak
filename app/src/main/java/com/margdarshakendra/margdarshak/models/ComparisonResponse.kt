package com.margdarshakendra.margdarshak.models

data class ComparisonResponse(
    val `data`: Data?,
    val statusCode: Int,
    val success: Boolean
){
    data class Data(
        val `data`: List<DataX>
    ){
        data class DataX(
            val avg_marks: Int,
            val name: String,
            val userID: String
        )
    }
}