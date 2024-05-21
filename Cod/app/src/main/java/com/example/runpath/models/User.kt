package com.example.runpath.models

data class User(
    val userId: String? = null,
    val username: String = "",
    val password: String = "",
    val email: String = "",
    val dateCreated: String = ""
)
