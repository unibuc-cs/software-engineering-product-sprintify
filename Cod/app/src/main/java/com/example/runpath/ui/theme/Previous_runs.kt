package com.example.runpath.ui.theme

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.example.runpath.database.RunDAO
import com.example.runpath.database.SessionManager
import com.example.runpath.models.Run

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
            Text(text = "Run ${index + 1}: ${run.runId}")
        }
    }
}