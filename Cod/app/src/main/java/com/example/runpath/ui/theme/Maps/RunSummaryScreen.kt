// RunSummaryScreen.kt
package com.example.runpath.ui.theme.Maps  // Update with your actual package

import RunViewModel
import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.runpath.database.SessionManager
import com.example.runpath.ui.theme.Maps.BottomNavItem
import kotlinx.coroutines.delay


@SuppressLint("RememberReturnType")
@Composable
fun RunSummaryScreen(
    elapsedTime: Long,
    distance: Double,
    circuitId: String?,
    navController: NavController
) {
    val viewModel: RunViewModel = viewModel()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSuccess by viewModel.isSuccess.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    val userId = sessionManager.getsharedPreferences().getString(SessionManager.KEY_USER_ID, "N/A")!!

    var generalRating by rememberSaveable { mutableStateOf(0f) }
    var intensity by rememberSaveable { mutableStateOf(3) }
    var lightLevel by rememberSaveable { mutableStateOf(3) }
    var difficulty by rememberSaveable { mutableStateOf(3) }

    val isSavingLeaderboard by viewModel.isSavingLeaderboard


    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            viewModel.clearStates()
            navController.popBackStack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Run Completed!", style = MaterialTheme.typography.h4)

        Spacer(modifier = Modifier.height(24.dp))

        // Time Display
        Text(
            text = "Time: ${formatElapsedTime(elapsedTime)}",
            style = MaterialTheme.typography.h6
        )

        // Distance Display
//        Text(
//            text = "Distance: ${"%.2f".format(distance/1000)} km",
//            style = MaterialTheme.typography.h6
//        )

        // Circuit-specific ratings
        if (circuitId != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Circuit Evaluation", style = MaterialTheme.typography.h6)
            RatingItem("Intensity", intensity) { intensity = it }
            RatingItem("Light Level", lightLevel) { lightLevel = it }
            RatingItem("Difficulty", difficulty) { difficulty = it }
        }

        // General rating
        Spacer(modifier = Modifier.height(32.dp))
        Text("Overall Experience:", style = MaterialTheme.typography.h6)
        StarRating(rating = generalRating) { newRating ->
            generalRating = newRating
        }

        // Loading and error states
        if (isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colors.error)
        }

        Spacer(modifier = Modifier.height(32.dp))
        val errorMessage by viewModel.errorMessage.collectAsState()
        // Save Button
        Button(
            onClick = {
                if (circuitId != null) {
                    viewModel.saveCircuitRating(
                        circuitId = circuitId,
                        userId = userId,
                        intensity = intensity,
                        lightLevel = lightLevel,
                        difficulty = difficulty
                    )
                    viewModel.saveLeaderboardEntry(
                        circuitId = circuitId,
                        userId = userId,
                        time = elapsedTime,
                        distance = distance / 1000 // Convert meters to km
                    )
                }
            },
            enabled = !isLoading && !isSavingLeaderboard
        ) {
            val buttonText = when {
                isLoading -> "Saving Rating..."
                isSavingLeaderboard -> "Saving Run..."
                else -> "Save & Submit to Leaderboard"
            }
            Text(buttonText)
        }
        if (isLoading || isSavingLeaderboard) {
            CircularProgressIndicator()
        }

        if (errorMessage != null) {
            Text(errorMessage!!, color = Color.Red)
        }
    }
}

// Keep your existing StarRating and RatingItem composables

@Composable
fun StarRating(rating: Float, onRatingChanged: (Float) -> Unit) {
    Row {
        for (i in 1..5) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Star $i",
                tint = if (i <= rating) Color.Yellow else Color.Gray,
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onRatingChanged(i.toFloat()) }
            )
        }
    }
}
@Composable
fun RatingItem(label: String, value: Int, onValueChange: (Int) -> Unit) {
    Column {
        Text("$label: $value")
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 1f..5f,
            steps = 4
        )
    }
}
internal fun formatElapsedTime(millis: Long): String {
    val seconds = (millis / 1000) % 60
    val minutes = (millis / (1000 * 60)) % 60
    val hours = (millis / (1000 * 60 * 60))
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

//private fun saveRunData(time: Long, distance: Double, rating: Float, circuitId: String?) {
//    // Implement your save logic here (Room DB, Firestore, etc.)
//    // Example:
//    val runData = RunData(
//        duration = time,
//        distance = distance,
//        rating = rating,
//        circuitId = circuitId,
//        date = System.currentTimeMillis()
//    )
//    // repository.saveRun(runData)
//}

