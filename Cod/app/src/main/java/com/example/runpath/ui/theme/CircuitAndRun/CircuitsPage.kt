

import android.widget.ImageView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.runpath.database.CircuitDAO
import com.example.runpath.database.SessionManager
import com.example.runpath.models.Circuit
import com.squareup.picasso.Picasso


//function to craete a snapshot of the circuit map
@Composable
fun CircuitMap(circuit: Circuit): String {
    // creez un URL pentru harta statica a circuitului
    val apiKey = "AIzaSyBcDs0jQqyNyk9d1gSpk0ruLgvbd9pwZrU"
    val baseUrl = "https://maps.googleapis.com/maps/api/staticmap"
    val size = "900x1500"
    val zoom = "16"
    val path = circuit.route?.joinToString("|") { "${it.latitude},${it.longitude}" }
    val style = "feature:all|element:labels|visibility:off"
    val color = "0xFF0000" // portocaliu

    return "$baseUrl?size=$size&zoom=$zoom&path=color:$color|$path&style=$style&key=$apiKey"
}

@Composable
fun CircuitsPage(navController: NavController, sessionManager: SessionManager,runSelected : Boolean = false) {
    // variables for the circuits page
    val circuitDao = CircuitDAO()
    var circuits by remember { mutableStateOf(listOf<Circuit>()) }
    var showDialog by remember { mutableStateOf(false) }
    var currentCircuit by remember { mutableStateOf<Circuit?>(null) }
    var selectedFilter by remember { mutableStateOf("") }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    val filters = listOf("Distance", "Intensity", "Rating", "Difficulty")

    DisposableEffect(Unit) {// active listener for circuits
        val listenerRegistration = circuitDao.listenForCircuits { updatedCircuits ->
            circuits = updatedCircuits
        }

        onDispose {
            listenerRegistration.remove()
        }
    }
    // display the circuits page
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Button(onClick = {isDropdownExpanded = true}) {
            Text("Filters")
        }

        DropdownMenu(
            expanded = isDropdownExpanded,
            onDismissRequest = { isDropdownExpanded = false }
        ) {
            filters.forEach { filter ->
                DropdownMenuItem(onClick = {
                    selectedFilter = filter
                    isDropdownExpanded = false
                }) {
                    Text(filter)
                }
            }
        }

        // sort the circuits based on the selected filter
        when (selectedFilter) {
            "Distance" -> circuits = circuits.sortedBy { it.distance }
            "Intensity" -> circuits = circuits.sortedBy { it.intensity }
            "Rating" -> circuits = circuits.sortedBy { it.rating }
            "Difficulty" -> circuits = circuits.sortedBy { it.difficulty }
        }
        //text for the selected filter
        Text(
            text = "Current filter: $selectedFilter",
            modifier = Modifier.padding(top = 50.dp).align(Alignment.TopEnd),
            fontWeight = FontWeight.Bold,
        )


        // display the circuits
        Text(
            text = "Circuits",
            modifier = Modifier.padding(16.dp).align(Alignment.TopCenter),
            fontWeight = FontWeight.Bold,
        )

        LazyColumn(
            modifier = Modifier
                .padding(top = 40.dp)
        ) {
            //iterate through the circuits
            itemsIndexed(circuits) { index, circuit ->
                Column {
                    Text(
                        text = circuit.name
                    )
                    if(runSelected){
                        Button(onClick = {
                            // convert the circuit's route to a string
                            val route = circuit.route?.joinToString("|") { "${it.latitude},${it.longitude}" }
                            val circuitId = circuit.circuitId
                            // navigate to the MapScreen and pass the route as a parameter in the route string
                            navController.navigate("mapPage/route=$route/circuitId=$circuitId")
                        }) {
                            Text("Select")
                        }
                    }
                    Button(onClick = {
                        currentCircuit = circuit
                        showDialog = true
                    }) {
                        Text("See Details")
                    }

                    val route = circuit.route
                    // display the circuit map
                    if (route != null) {
                        val imageUrl = CircuitMap(circuit = circuit)
                        AndroidView(
                            factory = { context ->
                                ImageView(context).apply {
                                    Picasso.get().load(imageUrl).into(this)
                                }
                            },
                            //we use picasso to load the image
                            update = { view ->
                                Picasso.get().load(imageUrl).into(view)
                            },
                            modifier = Modifier.width(1500.dp).height(500.dp)
                        )
                    }
                }
            }
        }

        // when the user clicks on the "See Details" button, a dialog will pop up with the circuit's details
        if (showDialog) {
            AlertDialog(
                //pop up for the circuit details
                onDismissRequest = { showDialog = false },
                title = { Text(text = currentCircuit?.name ?: "") },
                text = {
                    Column {
                        Text(text = buildAnnotatedString {
                            append("Description: ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(currentCircuit?.description ?: "")
                            }
                        })
                        Text(text = buildAnnotatedString {
                            append("\nDistance: ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("${currentCircuit?.distance ?: 0.0} km")
                            }
                        })
                        Text(text = buildAnnotatedString {
                            append("Estimated Time: ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(currentCircuit?.estimatedTime ?: "")
                            }
                        })
                        Text(text = buildAnnotatedString {
                            append("Intensity: ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("${currentCircuit?.intensity ?: 0}")
                            }
                        })
                        Text(text = buildAnnotatedString {
                            append("Terrain: ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(currentCircuit?.terrain ?: "")
                            }
                        })
                        Text(text = buildAnnotatedString {
                            append("Pet Friendly: ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(if (currentCircuit?.petFriendly == true) "Yes" else "No")
                            }
                        })
                        Text(text = buildAnnotatedString {
                            append("Light Level: ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("${currentCircuit?.lightLevel ?: 0}")
                            }
                        })
                        Text(text = buildAnnotatedString {
                            append("Rating: ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("${currentCircuit?.rating ?: 0.0}")
                            }
                        })
                        Text(text = buildAnnotatedString {
                            append("Difficulty: ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("${currentCircuit?.difficulty ?: 0}")
                            }
                        })
                    }
                },
                confirmButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}