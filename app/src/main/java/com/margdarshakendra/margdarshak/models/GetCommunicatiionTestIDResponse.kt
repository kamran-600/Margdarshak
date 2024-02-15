package com.margdarshakendra.margdarshak.models

data class GetCommunicatiionTestIDResponse(
    val statusCode: Int,
    val success: Boolean,
    val test_data: TestData?
){
    data class TestData(
        val hiretestID: Int,
        val test: String?,
        val testID: Int,
        val testime: String?,
        val testopic: String,
        val userID: String
    )
}