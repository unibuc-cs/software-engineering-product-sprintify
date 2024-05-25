package com.example.runpath.models


import com.google.maps.model.LatLng

data class Circuit (
    val circuitId: String? = null,
    val name: String,
    val description: String,
    val distance: Double,
    val estimatedTime: String,
    val intensity: Int,
    val terrain: String,
    val petFriendly: Boolean,
    val lightLevel: Int,
    val rating: Double,
    val difficulty: Int,
    val route: List<LatLng>
)