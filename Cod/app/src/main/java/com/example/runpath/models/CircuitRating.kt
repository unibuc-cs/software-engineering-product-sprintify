package com.example.runpath.models

data class CircuitRating(
    var circuitRatingId: String = "", // Firestore document ID
    var circuitId: String = "",
    var userId: String = "",
    var intensity: Int = 3,      // Default value
    var lightLevel: Int = 3,     // Default value
    var difficulty: Int = 3,     // Default value
    var timestamp: Long = System.currentTimeMillis()
) {
    // Required no-argument constructor
    constructor() : this("", "", "", 3, 3, 3, 0)
}