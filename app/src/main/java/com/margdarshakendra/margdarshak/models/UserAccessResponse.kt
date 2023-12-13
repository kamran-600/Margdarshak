package com.margdarshakendra.margdarshak.models

data class UserAccessResponse(
    val statusCode: Int,
    val success: Boolean,
    val profile: Profile,
    val links: List<Link>
) {
    data class Profile(
        val userID: String,
        val name: String,
        val email: String,
        val mobile: String,
        val pic: String,
        val usertype: String
    )

    data class Link(
        val link_group: String,
        val link: String,
        val mobileapp_id: String?
    )
}