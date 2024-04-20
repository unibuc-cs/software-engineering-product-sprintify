package com.example.runpath.models

import java.time.Instant
import kotlin.time.Duration

data class Run (
    val runId: Int,
    val userId: Int,
    val circuitId: Int,
    val startTime: Instant,
    val endTime: Instant,
    val pauseTime: Duration,
    val timeTracker: Duration,
    val paceTracker: Double,
    val distanceTracker: Double
    )