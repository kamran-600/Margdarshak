package com.example.margdarshakendra.models

data class UserDetailResponse(
    val `data`: Data,
    val statusCode: Int,
    val success: Boolean
) {

    data class Data(
        val email: String,
        val mobile: String,
        val name: String,
        val profile: Profile
    ) {
        data class Profile(
            val `class`: Any,
            val country_code: String,
            val details: String,
            val districtID: String,
            val dob: String,
            val gender: String,
            val inst: Any,
            val lang: String,
            val pic: String,
            val pincode: String,
            val qualification: String,
            val specialise: String
        )
    }
}