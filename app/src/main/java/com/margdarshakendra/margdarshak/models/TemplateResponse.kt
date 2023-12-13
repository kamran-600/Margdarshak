package com.margdarshakendra.margdarshak.models

data class TemplateResponse(
    val statusCode: Int,
    val success: Boolean,
    val template: String,
    val variables: String,
    val subject: String
)