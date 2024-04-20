package com.example.runpath.models

data class Profile(
    val userId: Int,
    val preferredTerrain: Int,
    val preferredLightLevel: Int,
    val isPetOwner: Boolean
)