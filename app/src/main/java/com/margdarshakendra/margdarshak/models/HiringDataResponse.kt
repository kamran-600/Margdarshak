package com.margdarshakendra.margdarshak.models

data class HiringDataResponse(
    val `data`: ApiData,
    val statusCode: Int,
    val success: Boolean,
    val employers: List<Employer?>?
) {

    data class ApiData(
        val current_page: Int,
        val data: List<HiringData>,
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
    ) {
        data class HiringData(
            val userID: Int,
            val hireID: Int?,
            val usertype: String,
            val `class`: String?,
            val inst: String?,
            val lang: String?,
            val pic: String?,
            val name: String,
            val state: String?,
            val district: String?,
            val pincode: String?,
            val advisor_name: String?,
            val position: String?,
            val email_verified: String,
            val mobile_verified: String,
            val log_count: Int,
            val remarks: String,
            val status: String,
            val date_follow: String,
            val lateDays: Int?,
            val remaining_call_count: Int?,
            val remaining_email_count: Int,
            val remaining_sms_count: Int,
            val remaining_whatsapp_count: Int

        )

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