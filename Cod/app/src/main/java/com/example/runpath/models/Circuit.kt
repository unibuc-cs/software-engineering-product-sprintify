package com.example.runpath.models


import com.example.runpath.others.MyLatLng
import com.google.android.gms.maps.model.LatLng

data class Circuit (
    val circuitId: String? = null,
    val name: String = "",
    val description: String = "",
    val distance: Double = 0.0,
    val estimatedTime: String = "",
    val intensity: Int = 0,
    val terrain: String = "",
    val petFriendly: Boolean = false,
    val lightLevel: Int = 0,
    val rating: Double = 0.0,
    val difficulty: Int = 0,
    val route: List<MyLatLng> = listOf()
)