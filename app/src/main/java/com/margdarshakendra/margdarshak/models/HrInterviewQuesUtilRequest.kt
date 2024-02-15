package com.margdarshakendra.margdarshak.models

data class HrInterviewQuesUtilRequest(
    val mode: String,
    val resid: Int,
    val hrquestID: Int?=null,
    val rank: Int?= null,
    val remark: String?= null,
    val result : String?= null
)