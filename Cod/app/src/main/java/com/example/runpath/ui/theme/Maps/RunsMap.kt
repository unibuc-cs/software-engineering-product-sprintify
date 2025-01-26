import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Looper
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.runpath.R
import com.example.runpath.ui.theme.Maps.OrientationListener
import com.example.runpath.ui.theme.Maps.calculateDistance
import com.google.android.gms.location.*
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*

@SuppressLint("MissingPermission")
@Composable
fun RunsMap(
    initialRoute: List<LatLng>,
    onRouteCompleted: (Long, Double) -> Unit // Modified callback
) {

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val currentLocation = remember { mutableStateOf<LatLng?>(null) }
    val remainingRoute = remember { mutableStateListOf(*initialRoute.toTypedArray()) }
    val cameraPositionState = rememberCameraPositionState()

    // Camera position
    val cameraPosition = remember { mutableStateOf<LatLng?>(null) }

    // Camera tilt
    val cameraTilt = remember { mutableStateOf(0f) } // initial tilt is 0

    OrientationListener.initialize(context)

    // Variable for the current bearing of the camera
    var currentBearing by remember { mutableStateOf(0f) }
    fun animateCameraPosition(
        currentLatLng: LatLng,
        currentBearing: Float,
        targetLatLng: LatLng,
        targetBearing: Float,
        tilt: Float,
        zoom: Float,
        duration: Long = 1000 // Animation duration in milliseconds
    ) {
        val startLatLng = currentLatLng
        val startBearing = currentBearing

        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = duration
        animator.addUpdateListener { animation ->
            val fraction = animation.animatedValue as Float

            val interpolatedLatLng = LatLng(
                OrientationListener.lerp(startLatLng.latitude.toFloat(), targetLatLng.latitude.toFloat(), fraction).toDouble(),
                OrientationListener.lerp(startLatLng.longitude.toFloat(), targetLatLng.longitude.toFloat(), fraction).toDouble()
            )
            val interpolatedBearing = OrientationListener.adjustAngle(startBearing, targetBearing, fraction)

            val cameraPosition = CameraPosition.Builder()
                .target(interpolatedLatLng)
                .bearing(interpolatedBearing)
                .tilt(tilt)
                .zoom(zoom)
                .build()

            cameraPositionState.position = cameraPosition
        }
        animator.start()
    }
    LaunchedEffect(currentLocation.value, OrientationListener.getAzimuth(), cameraTilt.value) {
        val location = currentLocation.value
        location?.let {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                val bearing = location?.bearing ?: OrientationListener.getAzimuth()
                val targetLatLng = LatLng(location.latitude, location.longitude)
                animateCameraPosition(
                    currentLatLng = LatLng(cameraPositionState.position.target.latitude, cameraPositionState.position.target.longitude),
                    currentBearing = cameraPositionState.position.bearing,
                    targetLatLng = targetLatLng,
                    targetBearing = bearing,
                    tilt = cameraTilt.value,
                    zoom = 16f
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            currentLocation.value = LatLng(location.latitude, location.longitude)
        }
    }
    //request location updates every 3 seconds
    val locationRequest = LocationRequest.create().apply {
        interval = 3000
        fastestInterval = 1000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    val recordedLocations = remember { mutableStateListOf<LatLng>() }
    val startTime = remember { mutableStateOf(System.currentTimeMillis()) }
    DisposableEffect(Unit) {
        OrientationListener.startListening()
        val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                val newLocation = locationList.last()
                currentLocation.value = LatLng(newLocation.latitude, newLocation.longitude)
                recordedLocations.add(currentLocation.value!!) // Track all locations

                // Existing route checking logic...
                if (remainingRoute.isNotEmpty()) {
                    val nearestIndex = remainingRoute.indexOfFirst { point ->
                        val distance = calculateDistance(currentLocation.value!!, point) * 1000
                        distance < 10  // delete points that are within 5 meters


                        }
                    if (nearestIndex != -1) {
                        remainingRoute.removeAll(remainingRoute.take(nearestIndex + 1))
                    }
                }
                if (remainingRoute.isEmpty()) {
                    val elapsedTime = System.currentTimeMillis() - startTime.value
                    val totalDistance = calculateTotalDistance(recordedLocations)
                    onRouteCompleted(elapsedTime, totalDistance)
                }
            }
        }
    }

    //request location updates
        fusedLocationClient.requestLocationUpdates(
            LocationRequest.create().apply {
                interval = 3000
                fastestInterval = 1000
                priority = PRIORITY_HIGH_ACCURACY
            },
            locationCallback,
            Looper.getMainLooper()
        )

        // Clean up when the composable is disposed
        onDispose {
            OrientationListener.stop()
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    //generate the google map
    GoogleMap(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 56.dp),
        cameraPositionState = cameraPositionState
    ) {
        //marker for the current location
        currentLocation.value?.let {
            placeMarker(it, "Current Location")
        }
        //draw the route for the remaining route
        if (remainingRoute.isNotEmpty()) {
            Polyline(points = remainingRoute.toList(), color = Color.Red, width = 10f)
        }
    }
}

//place a marker on the map
@Composable
fun placeMarker(location: LatLng, title: String) {
    Marker(
        state = MarkerState(position = location),
        title = title,
        snippet = "Marker at $title",
        icon = BitmapDescriptorFactory.fromResource(R.drawable.current_location_icon)
    )
}

private fun calculateTotalDistance(locations: List<LatLng>): Double {
    var total = 0.0
    for (i in 1 until locations.size) {
        total += calculateDistance(locations[i-1], locations[i])
    }
    return total
}