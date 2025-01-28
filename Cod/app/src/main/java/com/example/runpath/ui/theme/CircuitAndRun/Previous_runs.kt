package com.example.runpath.ui.theme.CircuitAndRun

import android.app.Dialog
import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.TextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.runpath.database.CommunityDAO
import com.example.runpath.database.PostDAO
import com.example.runpath.database.RunDAO
import com.example.runpath.database.SessionManager
import com.example.runpath.models.Community
import com.example.runpath.models.Post
import com.example.runpath.models.Run
import com.squareup.picasso.Picasso
import java.time.LocalDateTime

@Composable
fun Previous_runs(navController: NavController, sessionManager: SessionManager, runSelected : Boolean = false) {
    // Get the user id
    val runDAO = RunDAO()
    val userId = sessionManager.getsharedPreferences().getString(SessionManager.KEY_USER_ID, "N/A")!!

    // Create a state variable to hold the list of runs
    var runs by remember { mutableStateOf(listOf<Run>()) }

    var showDialog by remember { mutableStateOf(false)}
    var selectedRun by remember { mutableStateOf<Run?>(null)}
    var postContent by remember { mutableStateOf("")}
    var selectedCommunityId by remember { mutableStateOf("")}
    var showCommunityDropdown by remember { mutableStateOf(false)}
    var joinedCommunities by remember { mutableStateOf(listOf<Community>())}
    val communityDAO = CommunityDAO()

    // Create a listener for the runs
    DisposableEffect(Unit) {
        val listenerRegistration = runDAO.listenForRuns(userId) { updatedRuns ->
            runs = updatedRuns
        }

        onDispose {
            listenerRegistration.remove()
        }
    }

    DisposableEffect(userId) {
        communityDAO.getCommunities { allCommunities ->
            allCommunities.forEach { community ->
                communityDAO.isUserMemberOfCommunity(community.communityId ?: "", userId) { isUserMember ->
                    if(isUserMember) {
                        joinedCommunities = joinedCommunities + community
                    }
                }
            }
        }
        onDispose {  }
    }

    // Display the runs
    LazyColumn {
        itemsIndexed(runs) { index, run ->
            // Display each run
            // You can customize this part to display the run data as you want

            Text(text = "Run " + (index + 1) + ": " + run.startTime + " - " + run.endTime + " - " + run.distanceTracker + " km")

            if(runSelected) {
                Button(onClick = {

                    val route = run.coordinate.joinToString("|") { "${it.latitude},${it.longitude}" }
                    navController.navigate("previous_run/route=$route")

                    //selectedRun = run
                    //showDialog = true

                }) {
                    Text("Select")
                }
            }

            if (run.coordinate.isNotEmpty()) {
                println("we have coordinates")
                val image = RunMap(run = run)
                AndroidView(
                    factory = { context ->
                        ImageView(context).apply {
                            Picasso.get().load(image).into(this)
                        }
                    },
                    modifier = Modifier
                        .width(1000.dp)
                        .height(500.dp)
                )
            } else {
                Text("No coordinates for this run")
            }

            // Post button
            Button(onClick = {
                selectedRun = run
                showDialog = true
            }) {
                Text("Post")
            }
        }
    }

    if (showDialog && selectedRun != null) {
        Dialog(onDismissRequest = { showDialog = false}) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color.White),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = postContent,
                    onValueChange = { postContent = it},
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(onClick = { showCommunityDropdown = true
                                    println("showCommunityDropdown: $showCommunityDropdown")}) {
                    Text(joinedCommunities.find {it.communityId == selectedCommunityId}?.name ?: "Select Community")
                }
                DropdownMenu(
                    expanded = showCommunityDropdown,
                    onDismissRequest = { showCommunityDropdown = false}
                ) {
                    joinedCommunities.forEach { community ->
                        DropdownMenuItem(onClick = {
                            selectedCommunityId = community.communityId ?: ""
                            showCommunityDropdown = false
                        }) {
                            Text(community.name)
                        }
                    }
                }
                Button(onClick = {
                    val mapImageUrl = generateMapUrl(run = selectedRun!!)

                    val post = Post(
                        userId = userId,
                        author = sessionManager.getsharedPreferences().getString(SessionManager.KEY_USERNAME, "Anonymous")!!,
                        content = "Run on ${selectedRun!!.startTime}: $postContent",
                        timestamp = LocalDateTime.now().toString(),
                        communityId = selectedCommunityId,
                        mapImageUrl = mapImageUrl,
                        routeCoordinates = selectedRun!!.coordinate
                    )
                    PostDAO().insertPost(post) {
                        showDialog = false
                        postContent = ""
                        selectedCommunityId = ""
                    }
                }) {
                    Text("Post")
                }
            }
        }
    }
}

fun generateMapUrl(run: Run): String {
    // Create a static map URL for the run's path
    val apiKey = "AIzaSyA-ex_X39_7yXyoxV-GlG0M0pVok_Rv5x8"
    val baseUrl = "https://maps.googleapis.com/maps/api/staticmap"
    val size = "900x1500"
    val distance = run.distanceTracker
    val zoom = when {
        distance < 1 -> "16"
        distance < 5 -> "14"
        distance < 10 -> "13"
        distance < 20 -> "12"
        distance < 40 -> "11"
        else -> "10"
    }
    val path = run.coordinate?.joinToString("|") { "${it.latitude},${it.longitude}" }
    val style = "feature:all|element:labels|visibility:off"
    val color = "0xFF0000" // Red color for the path

    return "$baseUrl?size=$size&zoom=$zoom&path=color:$color|$path&style=$style&key=$apiKey"
}

@Composable
fun RunMap(run: Run): String {
    // creez un URL pentru harta statica a circuitului
    val apiKey = "AIzaSyA-ex_X39_7yXyoxV-GlG0M0pVok_Rv5x8"
    val baseUrl = "https://maps.googleapis.com/maps/api/staticmap"
    val size = "900x1500"
    val distance = run.distanceTracker
    val zoom = when {
        distance < 1 -> "16"
        distance < 5 -> "14"
        distance < 10 -> "13"
        distance < 20 -> "12"
        distance < 40 -> "11"
        else -> "10"
    }
    val path = run.coordinate?.joinToString("|") { "${it.latitude},${it.longitude}" }
    val style = "feature:all|element:labels|visibility:off"
    val color = "0xFF0000" // portocaliu

    return "$baseUrl?size=$size&zoom=$zoom&path=color:$color|$path&style=$style&key=$apiKey"
}