package com.example.margdarshakendra.models

import java.io.File

data class UserUpdateRequest(
    val institute: String,
    val district: Int,
    val zipcode: Int,
    val country: String,
    val classorexam: String,
    val newemail: String,
    val dob: String,
    val fileupload: File,
    val details: String,
    val gender: String,
    val qualification: String,
    val specialization: String,
    val mobile: String,
    val usertype: String,
    val fullname: String,
    val preflanguage: String

    )