package com.example.runpath.models

import com.example.runpath.others.MyLatLng
import java.time.Instant
import kotlin.time.Duration

data class Run (
    val runId: String? = null,
    val userId: String? = null,
    val circuitId: String? = null,
    val startTime: String = "",
    val endTime: String = "",
    val pauseTime: String = "",
    val timeTracker: String = "",
    val paceTracker: Double = 0.0,
    val distanceTracker: Double = 0.0,
    val coordinate : List<MyLatLng> = listOf()
    )