package com.example.runpath.models

data class PreviousRuns(
    val runId: String? = null,
    val userId: String? = null,
    val circuitId: String? = null,
    val startTime : String? = null,
    val endTime : String? = null,
    val pauseTime : String? = null,
    val timeTracker : String? = null,
    val paceTracker : String? = null,
    val distanceTracker : String? = null
)