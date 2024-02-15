package com.margdarshakendra.margdarshak.models

data class UserAccessResponse(
    val statusCode: Int,
    val success: Boolean,
    val profile: Profile,
    val links: List<Link>,
    val notifications: List<Notification>,
    val associate: Int
) {
    data class Profile(
        val userID: String,
        val name: String,
        val email: String,
        val mobile: String,
        val pic: String,
        val usertype: String,
        val meet_link:String?
    )

    data class Link(
        val link_group: String,
        val link: String,
    )

    data class Notification(
        val notifyID: Int,
        val task_name: String,
        val task: String,
        val scheduled_time: String,
        val reminder: String,
        val reminder_count: String
    )
}