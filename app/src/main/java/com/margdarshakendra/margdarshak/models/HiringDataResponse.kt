package com.margdarshakendra.margdarshak.models

data class HiringDataResponse(
    val `data`: List<Data>,
    val statusCode: Int,
    val success: Boolean
){
    data class Data(
        val advisor_name: String,
        val date_follow: String,
        val district: String,
        val email: String,
        val email_verified: String,
        val hireID : Int,
        val log_count: Int,
        val lateDays: Int,
        val mobile: String,
        val mobile_verified: String,
        val name: String,
        val pic: String,
        val pincode: String,
        val position: String,
        val remaining_call_count: Any?,
        val remaining_email_count: Int,
        val remaining_sms_count: Int,
        val remaining_whatsapp_count: Int,
        val remarks: String,
        val state: String,
        val status: String,
        val userID: Int,
        val usertype: String
    )
}