package com.example.runpath.models



data class Leaderboard(
    val leaderboardId: String = "",
    val circuitId: String = "",
    val userId: String = "",
    val time: Long = 0L,       // Time in milliseconds
    val distance: Double = 0.0,// Distance in kilometers
    val timestamp: Long = 0L
)