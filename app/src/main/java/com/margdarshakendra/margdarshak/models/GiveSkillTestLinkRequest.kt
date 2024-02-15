package com.margdarshakendra.margdarshak.models

data class GiveSkillTestLinkRequest(
    val hire_id: String,
    val skill_id: String?= null,
    val t_date: String,
    val uid: String
)