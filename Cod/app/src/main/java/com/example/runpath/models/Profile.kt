package com.example.runpath.models

data class Profile(
    val userId: Int,
    val preferredTerrain: String,
    val preferredLightLevel: String,
    val isPetOwner: Boolean
)