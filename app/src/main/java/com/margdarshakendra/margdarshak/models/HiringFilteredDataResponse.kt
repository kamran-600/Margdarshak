package com.margdarshakendra.margdarshak.models

data class HiringFilteredDataResponse(
    val `data`: List<Data>,
    val statusCode: Int,
    val success: Boolean,
    val employers: List<Employer?>?
) {
    data class Data(
        val userID: Int,
        val hireID: Int?,
        val usertype: String,
        val pic: String?,
        val name: String,
        val state: String,
        val district: String,
        val pincode: String,
        val advisor_name: String?, // Nullable since it can be null in the JSON
        val position: String,
        val email_verified: String,
        val mobile_verified: String,
        val log_count: Int,
        val remarks: String,
        val status: String,
        val date_follow: String,
        val lateDays: Int,
        val remaining_call_count: Int?, // Nullable since it can be null in the JSON
        val remaining_email_count: Int,
        val remaining_sms_count: Int,
        val remaining_whatsapp_count: Int
    )

    data class Employer(
        val userID: Int,
        val name: String
    )

}