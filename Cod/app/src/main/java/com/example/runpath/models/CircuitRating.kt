package com.example.runpath.models

data class CircuitRating (
    val circuitRatingId : String? = null,
    val circuitId: String? = null,
    val userId: String? = null,
    val rating: Double = 0.0,
    val petFriendly : Boolean = false,
    val lightLevel : Int = 0,
)