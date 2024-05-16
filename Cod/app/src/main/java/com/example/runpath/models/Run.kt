package com.example.runpath.models

import java.time.Instant
import kotlin.time.Duration

data class Run (
    val runID: String? = null,
    val userID: String? = null,
    val circuitID: String? = null,
    val startTime: Instant,
    val endTime: Instant,
    val pauseTime: Duration,
    val timeTracker: Duration,
    val paceTracker: Double,
    val distanceTracker: Double
    )