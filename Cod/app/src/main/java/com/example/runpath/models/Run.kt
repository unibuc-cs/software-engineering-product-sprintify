package com.example.runpath.models

import java.time.Instant
import kotlin.time.Duration

data class Run (
    val runId: String? = null,
    val userId: String? = null,
    val circuitId: String? = null,
    val startTime: Instant,
    val endTime: Instant,
    val pauseTime: Duration,
    val timeTracker: Duration,
    val paceTracker: Double,
    val distanceTracker: Double
    )