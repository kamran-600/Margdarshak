package com.margdarshakendra.margdarshak.models

data class CounsellingDataResponse(
    val `data`: ApiData,
    val statusCode: Int,
    val success: Boolean,
    val employers: List<Employer?>?
) {

    data class ApiData(
        val current_page: Int,
        val data: List<CounsellingData>,
        val first_page_url: String,
        val from: Int,
        val last_page: Int,
        val last_page_url: String,
        val links: List<PageLink>,
        val next_page_url: String?,
        val path: String,
        val per_page: Int,
        val prev_page_url: String?,
        val to: Int,
        val total: Int
    ){
        data class CounsellingData(
                val advisor_name: String?,
                val `class`: String?,
                val date_follow: String,
                val district: String?,
                val email_verified: String,
                val inst: String?,
                val lang: String?,
                val log_count: Int?,
                val mobile_verified: String,
                val name: String,
                val pic: String?,
                val pincode: String?,
                val remaining_call_count: Int?,
                val remaining_email_count: Int,
                val remaining_sms_count: Int,
                val remaining_whatsapp_count: Int,
                val remarks: String,
                val state: String?,
                val status: String,
                val userID: Int,
                val usertype: String )

        data class PageLink(
            val url: String,
            val label: String,
            val active: Boolean
        )
    }

    data class Employer(
        val userID: Int,
        val name: String
    )
}