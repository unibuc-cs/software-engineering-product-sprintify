package com.example.runpath.ui.theme

import CircuitScreen
import CircuitsPage
import FreemodeScreen
import PreviousRunScreen
import RunPage
import com.example.runpath.ui.theme.ProfilePage

import android.annotation.SuppressLint
import android.app.UiModeManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.runpath.R
import com.example.runpath.database.SessionManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.GeoApiContext
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

// bara de navigare de jos
sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    data object Map : BottomNavItem("mapPage", Icons.Default.Home, "Map")
    data object Community : BottomNavItem("CommunityPage", Icons.Default.Star, "Community")

    data object Run : BottomNavItem("run", Icons.Default.Add, "Run")
    data object Circuit : BottomNavItem("circuit", Icons.Default.LocationOn, "Circuit")
    data object Profile : BottomNavItem("ProfilePage", Icons.Default.AccountBox, "Profile")
    companion object {
        val values = listOf(Map, Community, Run, Circuit, Profile)
    }
}


@Composable
fun BottomNavigationBar(navController: NavController) {
    BottomNavigation(
        backgroundColor = Color.Gray,
        contentColor = Color.White
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        BottomNavItem.values.forEach { item ->
            BottomNavigationItem(
                selected = currentRoute == item.route,

                onClick = {

                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(item.label) }
            )
        }
    }
}

// functii pentru permisiuni si locatie
fun savePermissionStatus(context: Context, isGranted: Boolean) {
    val sharedPreferences = context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putBoolean("LocationPermissionGranted", isGranted)
    editor.apply()
}

fun getPermissionStatus(context: Context): Boolean {
    val sharedPreferences = context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
    println(
        "LocationPermissionGranted: ${
            sharedPreferences.getBoolean(
                "LocationPermissionGranted",
                false
            )
        }"
    )
    return sharedPreferences.getBoolean("LocationPermissionGranted", false) // fals ca default
}

// functie pentru a cere permisiunea de locatie
@Composable
fun RequestLocationPermission(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
) {
    val context = LocalContext.current
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                savePermissionStatus(context, true) // salveaza statusul permisiunii
                onPermissionGranted()
            } else {
                savePermissionStatus(context, false)
                onPermissionDenied()
            }
        }
    )

    LaunchedEffect(Unit) {
        // verifica daca permisiunea a fost deja acordata
        if (getPermissionStatus(context)) {
            println("Permission was granted")
            // permisiunea a fost deja acordata
            onPermissionGranted()
        } else {
            locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}
// functie pentru a obtine locatia curenta
@SuppressLint("MissingPermission")
fun getCurrentLocation(
    fusedLocationClient: FusedLocationProviderClient,
    onLocationReceived: (Location) -> Unit
) {
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            onLocationReceived(location)
        } else {
            // construieste un nou request pentru locatie
            val locationRequest = LocationRequest.Builder(10000L) // seteaza intervalul la 10s
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxUpdateDelayMillis(5000L)
                .build()

            val locationCallback = object : com.google.android.gms.location.LocationCallback() {
                override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                    locationResult.locations.firstOrNull()?.let {
                        onLocationReceived(it)
                        fusedLocationClient.removeLocationUpdates(this) // eliminam callback-ul
                    }
                }
            }
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }.addOnFailureListener { e ->
        println("Error getting location: ${e.message}")
    }
}


// codul pentru live tracking

// data class pentru segment
data class Segment(val startIndex: Int, val endIndex: Int, val color: Color)

// functie pentru a plasa un marker pe harta
@Composable
fun placeMarker(location: LatLng, title: String) {
    Marker(
        state = MarkerState(position = location),
        title = title,
        snippet = "Marker at $title",
        icon = BitmapDescriptorFactory.fromResource(R.drawable.current_location_icon)
    )
}

// buton pentru a incepe/pauza tracking-ul
@Composable
fun RunControlButton(
    isRunActive: MutableState<Boolean>,
    startedRunningFlag: MutableState<Boolean>,
    locationPoints: SnapshotStateList<LatLng>,
    segments: SnapshotStateList<Segment>,
    onButtonClick: () -> Unit
) {
    val buttonText = if (isRunActive.value) "Pause Run" else "Start Run"

    Button(
        onClick = {
            if(!startedRunningFlag.value) {
                startedRunningFlag.value = true
            }
            val currentColor = if (isRunActive.value) Color.Red else Color.Blue

            if (segments.isNotEmpty()) {
                val lastSegment = segments.last()
                if (lastSegment.color != currentColor) {
                    segments.add(Segment(lastSegment.endIndex,locationPoints.size - 1, currentColor))

                }
            } else {
                segments.add(Segment(0, locationPoints.size - 1, currentColor))
            }

            onButtonClick()
            isRunActive.value = !isRunActive.value

        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = buttonText)
    }
}

// functie pentru a obtine locatia curenta si a incepe tracking-ul
@SuppressLint("MissingPermission")
fun getCurrentLocationAndTrack(
    fusedLocationClient: FusedLocationProviderClient,
    locationPoints: SnapshotStateList<LatLng>,
    segments: SnapshotStateList<Segment>,
    isRunActive: MutableState<Boolean>,
    startedRunningFlag: MutableState<Boolean>,
    currentLocation: MutableState<LatLng?>,
    steps: Int = 5
) {
    val locationRequest = LocationRequest.create().apply {
        // seteaza intervalul pentru a obtine locatia curenta a utilizatorului
        interval = 3000
        fastestInterval = 1000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
    // callback pentru locatie
    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                val newLocation = locationList.last()
                val newLatLng = LatLng(newLocation.latitude, newLocation.longitude)
                currentLocation.value = newLatLng
                val interpolatedPoints = if(locationPoints.isNotEmpty()) {
                    val lastPoint = locationPoints.last()
                    interpolatePoints(lastPoint, newLatLng, steps)
                } else {
                    emptyList()
                }
                locationPoints.addAll(interpolatedPoints)
                locationPoints.add(newLatLng)

                if(startedRunningFlag.value) {
                    if(segments.isNotEmpty()) {
                        val lastSegment = segments.last()
                        if(isRunActive.value && lastSegment.color == Color.Blue) {
                            segments.add(Segment(lastSegment.endIndex, locationPoints.size - 1, Color.Red))
                        } else if(!isRunActive.value && lastSegment.color == Color.Red) {
                            segments.add(Segment(lastSegment.endIndex, locationPoints.size - 1, Color.Blue))
                        } else {
                            segments[segments.lastIndex] = lastSegment.copy(endIndex = locationPoints.size - 1)
                        }
                    } else {
                        val initialColor = if(isRunActive.value) Color.Red else Color.Blue
                        segments.add(Segment(0, locationPoints.size - 1, initialColor))
                    }
                }
            }
        }
    }
    fusedLocationClient.requestLocationUpdates(
        locationRequest,
        locationCallback,
        Looper.getMainLooper()
    )
}

// functie pentru a obtine punctele de pe ruta
@Composable
fun placeMarkerOnMap(location: LatLng, title: String) {
    Marker(
        state = MarkerState(position = location),
        title = title,
        snippet = "Marker at $title"
    )
}
// functie pentru a afisa harta
@Composable
fun GMap(
    currentLocation: MutableState<LatLng?>,
    searchedLocation: MutableState<LatLng?>,
    cameraPosition: MutableState<LatLng?>,
    locationPoints: SnapshotStateList<LatLng>,
    segments: SnapshotStateList<Segment>,
    startedRunningFlag: MutableState<Boolean>,
    cameraTilt: MutableState<Float>
) {
    val cameraPositionState = rememberCameraPositionState().apply {
        val initialLocation: LatLng = if (searchedLocation.value == null) {
            currentLocation.value ?: LatLng(0.0, 0.0) // default este 0,0 pentru latitudine si longitudine
        } else {
            searchedLocation.value!!
        }
        position = CameraPosition.builder()
            .target(initialLocation)
            .zoom(15f)
            .tilt(cameraTilt.value) // setez inclinatia camerei
            .build()
    }
    // creez un nou obiect MapsActivity
    val mapsActivity = MapsActivity()
    val routePoints = remember { mutableStateOf(listOf<LatLng>()) }
    // efect pentru a actualiza cameraPosition
    LaunchedEffect(key1 = currentLocation.value, key2 = searchedLocation.value) {
        if (currentLocation.value != null && searchedLocation.value != null) {
            routePoints.value =
                mapsActivity.getRoutePoints(currentLocation.value!!, searchedLocation.value!!)
        }
    }
    LaunchedEffect(cameraPosition.value) {
        cameraPosition.value?.let {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 15f)
        }
    }

    val context = LocalContext.current
    val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
    val isNightMode = when (uiModeManager.nightMode) {
        UiModeManager.MODE_NIGHT_YES -> true
        else -> false
    }
    val mapProperties: MapProperties
    val mapStyle = if (isNightMode) {
        mapProperties = remember {
            MapProperties(
                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.dark_mode_map)
            )
        }
    } else {
        mapProperties = remember {
            MapProperties(
                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.light_mode_map)
            )
        }
    }
    // afisez harta
    GoogleMap(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 56.dp),
        cameraPositionState = cameraPositionState,
        properties = mapProperties,
        onMapLongClick = { latLng ->
            searchedLocation.value = latLng
        }
    ) {
        // marker pentru locatia curenta
        currentLocation.value?.let {
            placeMarker(
                location = it,
                title = "Current Location"
            )
        }

        // marker pentru locatia cautata
        searchedLocation.value?.let {
            placeMarkerOnMap(location = searchedLocation.value!!, title = "Searched Location")
        }

        if(startedRunningFlag.value) {
            segments.forEach {segment ->
                Polyline(
                    points = locationPoints.subList(segment.startIndex, segment.endIndex + 1),
                    color = segment.color,
                    width = 5f
                )
            }
        }

        if (routePoints.value.isNotEmpty()) { // afiseaza ruta pe harta sub forma unui polyline
            Polyline(
                points = routePoints.value,
                color = Color.Red,
                width = 10f
            )
        }

    }
}

// functie pentru a cauta locatii
@Composable
fun LocationSearchBar(
    placesClient: PlacesClient,
    searchedLocation: MutableState<LatLng?>
) {
    val context = LocalContext.current
    val geocoder = remember { Geocoder(context) }
    val searchQuery = remember { mutableStateOf("") }
    val suggestions = remember { mutableStateOf(listOf<AutocompletePrediction>()) }
    val showSuggestions = remember { mutableStateOf(true) }

    // efect pentru a cauta locatii
    LaunchedEffect(searchQuery.value) {
        if (showSuggestions.value) {
            val request = FindAutocompletePredictionsRequest.builder()
                .setQuery(searchQuery.value)
                .build()

            placesClient.findAutocompletePredictions(request).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val response = task.result
                    suggestions.value = response.autocompletePredictions
                } else {
                    // eroarea in cazul in care cautarea a esuat
                    println("Autocomplete prediction fetch failed: ${task.exception?.message}")
                }
            }
        }
    }

    // afiseaza campul de cautare
    Column {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = searchQuery.value,
            onValueChange = {
                searchQuery.value = it
                showSuggestions.value = true  // sugereaza locatii
            },
            label = { Text("Search location") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                val addresses: List<Address> =
                    geocoder.getFromLocationName(searchQuery.value, 1) ?: listOf()
                if (addresses.isNotEmpty()) {
                    val location = LatLng(addresses[0].latitude, addresses[0].longitude)
                    searchedLocation.value = location
                }
            })
        )
        // afiseaza sugestiile
        if (showSuggestions.value) {
            suggestions.value.forEach { prediction ->
                Text(
                    text = prediction.getFullText(null).toString(),
                    modifier = Modifier.clickable {
                        showSuggestions.value = false // Hide suggestions on click
                        val placeId = prediction.placeId
                        val placeFields = listOf(Place.Field.LAT_LNG)
                        val request = FetchPlaceRequest.newInstance(placeId, placeFields)

                        placesClient.fetchPlace(request).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val place = task.result
                                searchedLocation.value = place.place.latLng
                                searchQuery.value = prediction.getFullText(null)
                                    .toString() // Update the search bar text
                                showSuggestions.value =
                                    false  // Ensure it remains hidden after the update
                            } else {
                                // Handle the error.
                                println("Place fetch failed: ${task.exception?.message}")
                            }
                        }
                    }
                )
            }
        }
    }
}
// buton pentru a merge la locatia curenta
@Composable
fun CurrentLocationButton(
    currentLocation: MutableState<LatLng?>,
    cameraPosition: MutableState<LatLng?>,
    fusedLocationClient: FusedLocationProviderClient
) {

    Button(onClick = {
        getCurrentLocation(fusedLocationClient) { location ->
            val latLng = LatLng(location.latitude, location.longitude)
            currentLocation.value = latLng
            cameraPosition.value = currentLocation.value
            println("Current location: ${currentLocation.value}")
        }
    }) {
        Text("Go to Current Location")
    }

}

@Composable
fun TiltButton(cameraTilt: MutableState<Float>) {

    Button(onClick = {
        // schimba inclinatia camerei din 0 in 45 si invers
        cameraTilt.value = if (cameraTilt.value == 0f) 45f else 0f
    }) {
        Text("Toggle Tilt")
    }

}
// ecranul pentru harta
@Composable
fun MapScreen(
    currentLocation: MutableState<LatLng?>,
    searchedLocation: MutableState<LatLng?>
) {
    val context = LocalContext.current
    val contextMap = GeoApiContext.Builder()
        .apiKey("AIzaSyBcDs0jQqyNyk9d1gSpk0ruLgvbd9pwZrU")
        .build()
    val apiKey = "AIzaSyBcDs0jQqyNyk9d1gSpk0ruLgvbd9pwZrU"
    Places.initialize(context, apiKey)
    val placesClient = Places.createClient(context)
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationPoints = remember { mutableStateListOf<LatLng>() }
    val segments = remember { mutableStateListOf<Segment>() }
    val isRunActive = remember { mutableStateOf(false) }

    //camera position
    val currentSegmentId = remember { mutableIntStateOf(0) }
    val cameraPosition = remember { mutableStateOf<LatLng?>(null) }

    //tilt
    val cameraTilt = remember { mutableStateOf(0f) } // inclinarea initila este 0

    val startedRunningFlag = remember {mutableStateOf(false)}

    RequestLocationPermission(
        onPermissionGranted = {
            getCurrentLocation(fusedLocationClient) { location ->
                val latLng = LatLng(location.latitude, location.longitude)
                currentLocation.value = latLng
                if (searchedLocation.value == null) {
                    searchedLocation.value = latLng // seteaza locatia curenta ca locatie cautata
                }

                getCurrentLocationAndTrack(
                    fusedLocationClient,
                    locationPoints,
                    segments,
                    isRunActive,
                    startedRunningFlag,
                    currentLocation
                )
            }
        },
        onPermissionDenied = {
            println("Permission denied")
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // search bar pentru locatii
        LocationSearchBar(
            placesClient = placesClient,
            searchedLocation = searchedLocation
        )

        Box(
            modifier = Modifier.weight(1f)
        ) {
            // afiseaza mapa si butoanele
            GMap(
                currentLocation = currentLocation,
                searchedLocation = searchedLocation,
                cameraPosition = cameraPosition,
                locationPoints = locationPoints,
                segments = segments,
                startedRunningFlag = startedRunningFlag,
                cameraTilt = cameraTilt
            )


            // start/pause button
            RunControlButton(
                isRunActive = isRunActive,
                startedRunningFlag = startedRunningFlag,
                locationPoints = locationPoints,
                segments = segments,
                onButtonClick = {}
            )

            // butoanelepentru locatia curenta si inclinatia camerei


            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart) // pozitioneaza butoanele in coltul din stanga jos
                    .padding(bottom = 56.dp) // padding optional

            ) {
                CurrentLocationButton(
                    currentLocation = currentLocation,
                    cameraPosition = cameraPosition,
                    fusedLocationClient = fusedLocationClient
                )
                TiltButton(cameraTilt = cameraTilt)
            }
        }
    }
}
@Composable
fun NavigationHost(navController: NavHostController) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    val currentLocation = remember { mutableStateOf<LatLng?>(null) }
    val searchedLocation = remember { mutableStateOf<LatLng?>(null) }

    NavHost(navController, startDestination = BottomNavItem.Map.route) {
        composable(BottomNavItem.Map.route) {
            MapScreen(currentLocation, searchedLocation)
        }
        composable(BottomNavItem.Community.route) {
            CommunityPage(navController, sessionManager)
        }
        composable(BottomNavItem.Run.route) {
            RunPage { option ->
                when(option) {
                    "From a Circuit" -> navController.navigate("circuitScreen")
                    "From a Previous Run" -> navController.navigate("previousRunScreen")
                    "Freemode" -> navController.navigate("freemodeScreen")
                }
            }
        }
        composable(BottomNavItem.Circuit.route) {
            CircuitsPage(navController, sessionManager)
        }
        composable(BottomNavItem.Profile.route) {
            ProfilePage(navController, sessionManager)
        }
        composable("circuitScreen") {
            CircuitsPage(navController, sessionManager, true)
        }
        composable("previousRunScreen") {
            PreviousRunScreen()
        }
        composable("freemodeScreen") {
            FreemodeScreen()
        }
        composable("userProfile/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            UserProfilePage(navController, username)
        }
    }
}

fun interpolatePoints(start: LatLng, end: LatLng, steps: Int): List<LatLng> {
    val latStep = (end.latitude - start.latitude) / steps
    val lngStep = (end.longitude - start.longitude) / steps

    return(1 until steps).map {
        LatLng(start.latitude + it * latStep, start.longitude + it * lngStep)
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainInterface() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        NavigationHost(navController = navController)

    }
}
