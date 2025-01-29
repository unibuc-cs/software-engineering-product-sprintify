import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.runpath.database.CircuitDAO
import com.example.runpath.database.SessionManager
import com.example.runpath.models.Circuit
import com.google.firebase.firestore.ListenerRegistration
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.viewinterop.AndroidView
import android.widget.ImageView
import com.squareup.picasso.Picasso
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight

@Composable
fun CircuitsPage(navController: NavController, sessionManager: SessionManager, runSelected: Boolean = false) {
    val circuitDao = CircuitDAO()
    var circuits by remember { mutableStateOf(listOf<Circuit>()) }
    var showDialog by remember { mutableStateOf(false) }
    var currentCircuit by remember { mutableStateOf<Circuit?>(null) }
    var selectedFilter by remember { mutableStateOf("") }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val filters = listOf("Distance", "Intensity", "Rating", "Difficulty")

    // Real-time listener for circuit updates
    DisposableEffect(Unit) {
        val listener: ListenerRegistration = circuitDao.listenForCircuits { updatedCircuits ->
            circuits = when (selectedFilter) {
                "Distance" -> updatedCircuits.sortedBy { it.distance }
                "Intensity" -> updatedCircuits.sortedByDescending { it.intensity }
                "Rating" -> updatedCircuits.sortedByDescending { it.rating }
                "Difficulty" -> updatedCircuits.sortedByDescending { it.difficulty }
                else -> updatedCircuits
            }
        }

        onDispose {
            listener.remove()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Filter UI
        Column(modifier = Modifier.align(Alignment.TopEnd)) {
            Button(
                onClick = { isDropdownExpanded = true },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Filters")
            }

            DropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false }
            ) {
                filters.forEach { filter ->
                    DropdownMenuItem(
                        text = { Text(filter) },
                        onClick = {
                            selectedFilter = filter
                            isDropdownExpanded = false
                        }
                    )
                }
            }

            Text(
                text = "Current filter: $selectedFilter",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // Circuits List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp)
        ) {
            items(circuits) { circuit ->
                CircuitCard(
                    circuit = circuit,
                    onSelect = {
                        val route = circuit.route?.joinToString("|") { "${it.latitude},${it.longitude}" }
                        navController.navigate("mapPage/route=$route/circuitId=${circuit.circuitId}")
                    },
                    onDetails = {
                        currentCircuit = circuit
                        showDialog = true
                    },
                    runSelected = runSelected
                )
            }
        }

        // Circuit Details Dialog
        currentCircuit?.let { circuit ->
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(circuit.name) },
                    text = {
                        Column {
                            RatingDisplay(value = circuit.intensity, label = "Intensity")
                            RatingDisplay(value = circuit.lightLevel, label = "Light Level")
                            RatingDisplay(value = circuit.difficulty, label = "Difficulty")

                            Text(buildAnnotatedString {
                                append("Distance: ")
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append("${"%.2f".format(circuit.distance)} km")
                                }
                            })

                            Text(buildAnnotatedString {
                                append("Estimated Time: ")
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(circuit.estimatedTime)
                                }
                            })

                            MapPreview(circuit)
                        }
                    },
                    confirmButton = {
                        Column {
                            Button(onClick = { showDialog = false }) {
                                Text("Close")
                            }
                            // Add leaderboard button
                            Button(
                                onClick = {
                                    showDialog = false
                                    navController.navigate("leaderboard/${circuit.circuitId}")
                                }
                            ) {
                                Text("View Leaderboard")
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun CircuitCard(
    circuit: Circuit,
    onSelect: () -> Unit,
    onDetails: () -> Unit,
    runSelected: Boolean
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(circuit.name, style = MaterialTheme.typography.headlineSmall)

            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StarRatingWithLabel(rating = circuit.intensity.toFloat(), label = "Intensity")
                StarRatingWithLabel(rating = circuit.lightLevel.toFloat(), label = "Light")
                StarRatingWithLabel(rating = circuit.difficulty.toFloat(), label = "Difficulty")
            }

            if (runSelected) {
                Button(
                    onClick = onSelect,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Select Circuit")
                }
            }

            Button(
                onClick = onDetails,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Details")
            }
        }
    }
}
@Composable
private fun StarRatingWithLabel(rating: Float, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        StarRating(rating = rating)
    }
}

@Composable
fun RatingDisplay(value: Int, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text("$label: ", modifier = Modifier.width(100.dp))
        StarRating(rating = value.toFloat())
        Text(" ($value/5)", style = MaterialTheme.typography.bodySmall)
    }
}

// Add this composable at the bottom of your CircuitsPage file
@Composable
private fun RatingBar(
    rating: Float,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row {
            repeat(5) { index ->
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = if (index < rating) Color(0xFFFFA500) else Color.LightGray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// Update the existing StarRating composable to match
@Composable
private fun StarRating(rating: Float) {
    Row {
        repeat(5) { index ->
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = if (index < rating) Color(0xFFFFA500) else Color.LightGray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun MapPreview(circuit: Circuit) {
    val imageUrl = CircuitMap(circuit)
    AndroidView(
        factory = { context ->
            ImageView(context).apply {
                Picasso.get().load(imageUrl).into(this)
            }
        },
        update = { view ->
            Picasso.get().load(imageUrl).into(view)
        },
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
    )
}

fun CircuitMap(circuit: Circuit): String {
    val apiKey = "AIzaSyA-ex_X39_7yXyoxV-GlG0M0pVok_Rv5x8"
    val baseUrl = "https://maps.googleapis.com/maps/api/staticmap"
    val size = "600x200"
    val zoom = "14"
    val path = circuit.route?.joinToString("|") { "${it.latitude},${it.longitude}" }
    val markers = circuit.route?.map { "color:red|label:•|${it.latitude},${it.longitude}" }?.joinToString("&markers=")

    return "$baseUrl?size=$size&zoom=$zoom&path=color:0xFF0000|weight:5|$path&markers=$markers&key=$apiKey"
}
