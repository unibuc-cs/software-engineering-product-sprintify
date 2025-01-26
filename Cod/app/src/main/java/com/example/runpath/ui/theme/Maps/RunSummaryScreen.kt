// RunSummaryScreen.kt
package com.example.runpath.ui.screens  // Update with your actual package

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.runpath.ui.theme.Maps.BottomNavItem

@Composable
fun RunSummaryScreen(
    elapsedTime: Long,
    distance: Double,
    circuitId: String?,
    navController: NavController
) {
    var rating by rememberSaveable { mutableStateOf(0f) }
    var intensity by rememberSaveable { mutableStateOf(3) }
    var lightLevel by rememberSaveable { mutableStateOf(3) }
    var difficulty by rememberSaveable { mutableStateOf(3) }
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
        Text(
            text = "Distance: ${"%.2f".format(distance/1000)} km",
            style = MaterialTheme.typography.h6
        )
        if (circuitId != null) {
            // Circuit-specific ratings
            Spacer(modifier = Modifier.height(16.dp))
            Text("Circuit Evaluation", style = MaterialTheme.typography.h6)

            RatingItem("Intensity", intensity) { intensity = it }
            RatingItem("Light Level", lightLevel) { lightLevel = it }
            RatingItem("Difficulty", difficulty) { difficulty = it }
        }
        Spacer(modifier = Modifier.height(32.dp))

        // Rating Section
        Text("Rate your run:", style = MaterialTheme.typography.h6)
        StarRating(rating = rating) { newRating ->
            rating = newRating
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Save Button
        Button(
            onClick = {
                //saveRunData(elapsedTime, distance, rating, circuitId)
                //we go to map screen
                navController.navigate(BottomNavItem.Map.route) {
                    popUpTo(BottomNavItem.Map.route) {
                        inclusive = true // Clears entire back stack
                    }
                    launchSingleTop = true
                }
            },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Save Run", fontSize = 18.sp)
        }
    }
}

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
private fun formatElapsedTime(millis: Long): String {
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

