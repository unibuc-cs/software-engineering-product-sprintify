package com.example.runpath.models

data class Post(
    val userID: String? = null,
    val postID: String? = null,
    val author: String,
    val timestamp: String,
    val content: String
)