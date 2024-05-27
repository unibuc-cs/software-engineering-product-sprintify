import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.runpath.R
import com.example.runpath.database.CircuitDAO
import com.example.runpath.database.SessionManager
import com.example.runpath.models.Circuit
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng as GoogleLatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch



@Composable
fun CircuitMap(circuits: List<Circuit>) {
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState()

    val mapProperties by remember {
        mutableStateOf(
            MapProperties(
                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.circuit_map)
            )
        )
    }

    // Store the latest circuits value using rememberUpdatedState
    val currentCircuits by rememberUpdatedState(newValue = circuits)

    LaunchedEffect(currentCircuits) {
        if (currentCircuits.isNotEmpty() && currentCircuits.first().route.isNotEmpty()) {
            val firstRoutePoint = currentCircuits.first().route!!.first()
            cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(GoogleLatLng(firstRoutePoint.lat, firstRoutePoint.lng), 10f))
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize().height(300.dp),
        properties = mapProperties,
        cameraPositionState = cameraPositionState
    ) {
        currentCircuits.forEach { circuit ->
            circuit.route?.let { route ->
                Polyline(
                    points = route.map { GoogleLatLng(it.lat, it.lng) },
                    color = Color.Red // Adjust color as needed
                )
            }
        }
    }
}

@Composable
fun CircuitsPage(navController: NavController, sessionManager: SessionManager) {

    val circuitDao = CircuitDAO()
    var circuits by remember { mutableStateOf(listOf<Circuit>()) }

    //polyline for the route
    DisposableEffect(Unit) {
        val listenerRegistration = circuitDao.listenForCircuits { updatedCircuits ->
            circuits = updatedCircuits
        }

        onDispose {
            listenerRegistration.remove()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Text(
            text = "Circuits",
            modifier = Modifier.padding(16.dp)
        )

        // Circuits feed
        LazyColumn(
            modifier = Modifier
                .padding(top = 40.dp)
        ) {
            itemsIndexed(circuits) { index, circuit ->
                Column {
                    Text(
                        text = circuit.name,
                        modifier = Modifier.padding(16.dp)
                    )

                    val route = circuit.route
                    if (route != null) {
                        // Call your CircuitMap Composable here
                        CircuitMap(circuits = listOf(circuit))
                    }
                }
            }
        }
    }
}
