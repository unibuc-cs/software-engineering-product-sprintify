package com.example.runpath.models

data class Profile(
    val userId: String = "",
    val preferredTerrain: Int = 0,
    val preferredLightLevel: Int = 0,
    val isPetOwner: Boolean = false
)