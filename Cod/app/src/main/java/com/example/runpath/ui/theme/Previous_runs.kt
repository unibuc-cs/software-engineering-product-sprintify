package com.example.runpath.ui.theme

import android.widget.ImageView
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.runpath.database.RunDAO
import com.example.runpath.database.SessionManager
import com.example.runpath.models.Circuit
import com.example.runpath.models.Run
import com.squareup.picasso.Picasso

@Composable
fun Previous_runs(navController: NavController, sessionManager: SessionManager) {
    // Get the user id
    val runDAO = RunDAO()
    val userId = sessionManager.getsharedPreferences().getString(SessionManager.KEY_USER_ID, "N/A")!!

    // Create a state variable to hold the list of runs
    var runs by remember { mutableStateOf(listOf<Run>()) }

    // Create a listener for the runs
    DisposableEffect(Unit) {
        val listenerRegistration = runDAO.listenForRuns(userId) { updatedRuns ->
            runs = updatedRuns
        }

        onDispose {
            listenerRegistration.remove()
        }
    }

    // Display the runs
    LazyColumn {
        itemsIndexed(runs) { index, run ->
            // Display each run
            // You can customize this part to display the run data as you want

            Text(text = "Run " + (index + 1) + ": " + run.startTime + " - " + run.endTime + " - " + run.distanceTracker + " km")

            if (run.coordinate.isNotEmpty()) {
                println("we have coordinates")
                val image = RunMap(run = run)
                AndroidView(
                    factory = { context ->
                        ImageView(context).apply {
                            Picasso.get().load(image).into(this)
                        }
                    },
                    modifier = Modifier.width(1000.dp).height(500.dp)
                )
            } else {
                Text("No coordinates for this run")
            }


        }
    }
}
@Composable
fun RunMap(run: Run): String {
    // creez un URL pentru harta statica a circuitului
    val apiKey = "AIzaSyBcDs0jQqyNyk9d1gSpk0ruLgvbd9pwZrU"
    val baseUrl = "https://maps.googleapis.com/maps/api/staticmap"
    val size = "900x1500"
    val zoom = "16"
    val path = run.coordinate?.joinToString("|") { "${it.latitude},${it.longitude}" }
    val style = "feature:all|element:labels|visibility:off"
    val color = "0xFF0000" // portocaliu

    return "$baseUrl?size=$size&zoom=$zoom&path=color:$color|$path&style=$style&key=$apiKey"
}