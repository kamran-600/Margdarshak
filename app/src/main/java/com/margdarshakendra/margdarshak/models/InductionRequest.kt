package com.margdarshakendra.margdarshak.models

data class InductionRequest(
    val date: String,
    val hireID: Int?,
    val induction_type: String,
    val meet_link: String,
    val time: String,
    val uid: Int,
    val user_reference: String
)