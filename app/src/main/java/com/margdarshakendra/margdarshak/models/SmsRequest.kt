package com.margdarshakendra.margdarshak.models

data class SmsRequest(
    val mode:String,
    val contact_type: String,
    val content: String?,
    val date: String,
    val hireID: Int?,
    val remark: String,
    val status: String?,
    val template_id: String?,
    val time: String,
    val uid: String
)