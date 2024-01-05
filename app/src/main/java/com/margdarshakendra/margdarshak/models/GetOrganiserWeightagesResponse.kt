package com.margdarshakendra.margdarshak.models

data class GetOrganiserWeightagesResponse(
    val statusCode: Int,
    val success: Boolean,
    val weightage: List<Weightage>
){
    data class Weightage(
        val limit_qustion: String,
        val weightage: String,
        val weightagename: String,
        val weightime: Double
    )
}