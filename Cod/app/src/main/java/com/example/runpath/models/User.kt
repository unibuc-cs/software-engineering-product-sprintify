package com.example.runpath.models

data class User(
    val userId: Int,
    val username: String,
    val passwordHash: String,
    val email: String,
    val dateCreated: String
)
