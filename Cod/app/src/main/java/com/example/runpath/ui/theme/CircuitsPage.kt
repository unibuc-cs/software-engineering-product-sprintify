import android.widget.ImageView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    val apiKey = "AIzaSyBcDs0jQqyNyk9d1gSpk0ruLgvbd9pwZrU"
    val baseUrl = "https://maps.googleapis.com/maps/api/staticmap"
    val size = "900x1500"
    val zoom = "16"
    val path = circuit.route?.joinToString("|") { "${it.lat},${it.lng}" }
    val style = "feature:all|element:labels|visibility:off"
    val color = "0xFFA500" // portocaliu

    return "$baseUrl?size=$size&zoom=$zoom&path=color:$color|$path&style=$style&key=$apiKey"
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
            modifier = Modifier.padding(16.dp).align(Alignment.TopCenter)
        )

        // Circuits feed
        LazyColumn(
            modifier = Modifier
                .padding(top = 40.dp)
        ) {
            itemsIndexed(circuits) { index, circuit ->
                Column {
                    Text(
                        text = circuit.name
                    )

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
    }


}