package com.example.runpath.models

import kotlin.time.Duration

data class Leaderboard (
    val leaderboardId: Int,
    val circuitId: Int,
    val userId: Int,
    val rank: Int,
    val time: Duration


)

