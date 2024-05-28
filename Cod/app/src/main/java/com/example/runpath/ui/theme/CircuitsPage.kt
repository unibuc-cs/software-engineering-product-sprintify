import android.widget.ImageView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.navigation.NavController
import com.example.runpath.database.CircuitDAO
import com.example.runpath.database.SessionManager
import com.example.runpath.models.Circuit
import androidx.compose.ui.viewinterop.AndroidView
import com.google.maps.model.LatLng
import com.squareup.picasso.Picasso


@Composable
fun CircuitMap(circuit: Circuit): String {  
    // creez un URL pentru harta statica a circuitului
    val apiKey = "AIzaSyBcDs0jQqyNyk9d1gSpk0ruLgvbd9pwZrU"
    val baseUrl = "https://maps.googleapis.com/maps/api/staticmap"
    val size = "900x1500"
    val zoom = "16"
    val path = circuit.route?.joinToString("|") { "${it.lat},${it.lng}" }
    val style = "feature:all|element:labels|visibility:off"
    val color = "0xFF0000" // portocaliu

    return "$baseUrl?size=$size&zoom=$zoom&path=color:$color|$path&style=$style&key=$apiKey"
}

@Composable
fun CircuitsPage(navController: NavController, sessionManager: SessionManager) {
    // variabile pentru circuit
    val circuitDao = CircuitDAO()
    var circuits by remember { mutableStateOf(listOf<Circuit>()) }
    var showDialog by remember { mutableStateOf(false) }
    var currentCircuit by remember { mutableStateOf<Circuit?>(null) }

    DisposableEffect(Unit) {// functie pentru a lua toate circuitele
        val listenerRegistration = circuitDao.listenForCircuits { updatedCircuits ->
            circuits = updatedCircuits
        }

        onDispose {
            listenerRegistration.remove()
        }
    }
    // afisarea paginii de circuite
    Box( 
        modifier = Modifier.fillMaxSize(),
    ) {
        Text(
            text = "Circuits",
            modifier = Modifier.padding(16.dp).align(Alignment.TopCenter)
        )

        LazyColumn(
            modifier = Modifier
                .padding(top = 40.dp)
        ) {
            itemsIndexed(circuits) { index, circuit ->
                Column {
                    Text(
                        text = circuit.name
                    )

                    Button(onClick = {
                        currentCircuit = circuit
                        showDialog = true
                    }) {
                        Text("See Details")
                    }

                    val route = circuit.route
                    if (route != null) {
                        val imageUrl = CircuitMap(circuit = circuit)
                        AndroidView(
                            factory = { context ->
                                ImageView(context).apply {
                                    Picasso.get().load(imageUrl).into(this)
                                }
                            },
                            modifier = Modifier.width(1500.dp).height(500.dp)
                        )
                    }
                }
            }
        }

        //cand apasam butonul se deschide popup-ul cu detaliile circuitului
        if (showDialog) {
            AlertDialog(
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