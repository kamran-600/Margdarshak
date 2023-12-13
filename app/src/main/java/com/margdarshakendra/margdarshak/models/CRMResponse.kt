package com.margdarshakendra.margdarshak.models

data class CRMResponse(
    val `data`: Data,
    val statusCode: Int,
    val success: Boolean
){
    data class Data(
        val email: List<Email>,
        val sms: List<Sms>,
        val status: List<Status>,
        val whatsapp: List<Whatsapp>
    ){
        data class Email(
            val template: String,
            val templateID: Int
        )

        data class Sms(
            val template: String,
            val templateID: Int
        )

        data class Status(
            val action: String,
            val status: String
        )

        data class Whatsapp(
            val template: String,
            val templateID: Int
        )
    }
}