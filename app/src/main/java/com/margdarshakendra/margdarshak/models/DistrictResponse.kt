package com.margdarshakendra.margdarshak.models

data class DistrictResponse(
    val `data`: List<Data>,
    val mode: String,
    val statusCode: Int
) {
    data class Data(
        val district: String,
        val districtID: Int
    )
}

