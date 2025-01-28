package com.example.runpath.models

import com.example.runpath.others.MyLatLng

data class Post(
    val userId: String = " ",
    val postId: String? = null,
    val author: String = "",
    val timestamp: String = "",
    val content: String = "",
    val communityId: String = "",
    val mapImageUrl: String? = null,
    val routeCoordinates: List<MyLatLng> = listOf()
)