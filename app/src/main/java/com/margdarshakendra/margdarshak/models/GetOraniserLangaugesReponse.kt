package com.margdarshakendra.margdarshak.models

data class GetOraniserLangaugesReponse(
    val language: List<Language>,
    val statusCode: Int,
    val success: Boolean
){
    data class Language(
        val language: String,
        val languageID: Int
    )
}