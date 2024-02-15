package com.margdarshakendra.margdarshak.models

data class HiringSkillsResponse(
    val skills: List<Skill>,
    val statusCode: Int,
    val success: Boolean
){
    data class Skill(
        val skill: String,
        val skillsID: Int
    )
}