package com.example.margdarshakendra.models

data class CountryResponse(
    val `data`: List<Data>,
    val mode: String,
    val statusCode: Int
){
    data class Data(
        val country: String,
        val country_code: String
    )
}