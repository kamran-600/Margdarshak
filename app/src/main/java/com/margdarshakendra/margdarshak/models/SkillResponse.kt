package com.margdarshakendra.margdarshak.models

data class SkillResponse(
    val statusCode: Int,
    val success: Boolean,
    val allskills: List<Allskill>
){
    data class Allskill(
        val neg_marks: String,
        val quest_count: String,
        val skill: String,
        val skills_id: String,
        val time: String,
        val total_marks: String
    )
}