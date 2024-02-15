package com.margdarshakendra.margdarshak.models

data class CommunicationTestsResponse(
    val hireTest: List<HireTest>,
    val statusCode: Int,
    val success: Boolean
){
    data class HireTest(
        val test: String,
        val testID: Int
    )
}