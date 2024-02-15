package com.margdarshakendra.margdarshak.models

data class WebEmailsResponse(
    val message: List<Message>,
    val statusCode: Int,
    val success: Boolean
){
    data class Message(
        val attach: String?,
        val body: String,
        val edate: String?,
        val email_date: String?,
        val email_from: String,
        val email_to: String,
        val mailID: Int,
        val mail_uid: String,
        val name: String?,
        val pic: String?,
        val replied: String,
        val subject: String?,
        val userID: String,

// for sentbox endpoint added
        val sentmailID: Int?,
        val unique_key: String?,
        val views: String?,
        val last_viewed: String?,
    )
}