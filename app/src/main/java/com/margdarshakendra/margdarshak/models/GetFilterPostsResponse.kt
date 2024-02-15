package com.margdarshakendra.margdarshak.models

data class GetFilterPostsResponse(
    val `data`: List<Data>,
    val statusCode: Int,
    val success: Boolean
){
    data class Data(
        val position: String,
        val postID: Int
    )
}