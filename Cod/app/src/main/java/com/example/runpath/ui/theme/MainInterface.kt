package com.example.runpath.ui.theme

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    data object Home : BottomNavItem("home", Icons.Default.Home, "Home")
    data object Community : BottomNavItem("home", Icons.Default.Star, "Community")

    data object Run : BottomNavItem("run", Icons.Default.Add, "Run")
    data object Circuit : BottomNavItem("circuit", Icons.Default.LocationOn, "Circuit")
    data object Profile : BottomNavItem("profile", Icons.Default.AccountBox, "Profile")
    companion object {
        val values = listOf(Home, Community, Run, Circuit, Profile)
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

@Composable
fun NavigationHost(navController: NavHostController) {
    NavHost(navController, startDestination = BottomNavItem.Home.route) {
        composable(BottomNavItem.Home.route) {
            MainInterface()
        }
        composable(BottomNavItem.Community.route) { /* Community Screen UI */ }
        composable(BottomNavItem.Run.route) { /* Run Screen UI */ }
        composable(BottomNavItem.Circuit.route) { /* Search Screen UI */ }
        composable(BottomNavItem.Profile.route) { ProfilePage() }
    }
}


fun savePermissionStatus(context: Context, isGranted: Boolean) {
    val sharedPreferences = context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putBoolean("LocationPermissionGranted", isGranted)
    editor.apply()
}

fun getPermissionStatus(context: Context): Boolean {
    val sharedPreferences = context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
    println("LocationPermissionGranted: ${sharedPreferences.getBoolean("LocationPermissionGranted", false)}")
    return sharedPreferences.getBoolean("LocationPermissionGranted", false) // Default to false
}


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
                savePermissionStatus(context, true) // Save permission status
                onPermissionGranted()
            } else {
                savePermissionStatus(context, false) // Save denial status
                onPermissionDenied()
            }
        }
    )

    LaunchedEffect(Unit) {
        // Check if the permission has already been granted
        if (getPermissionStatus(context)) {
            println("Permission was granted")
            //the permission is retrieved successfully
            onPermissionGranted()
        } else {
            locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}
@SuppressLint("MissingPermission")
fun getCurrentLocation(
    fusedLocationClient: FusedLocationProviderClient,
    onLocationReceived: (Location) -> Unit
) {
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            onLocationReceived(location)
        } else {
            // Constructing a location request with new builder pattern
            val locationRequest = LocationRequest.Builder(10000L) // Set interval
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxUpdateDelayMillis(5000L) // Similar to fastestInterval
                .build()

            val locationCallback = object : com.google.android.gms.location.LocationCallback() {
                override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                    locationResult.locations.firstOrNull()?.let {
                        onLocationReceived(it)
                        fusedLocationClient.removeLocationUpdates(this) // Remove updates after receiving location
                    }
                }
            }
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
    }.addOnFailureListener { e ->
        println("Error getting location: ${e.message}")
    }
}


@Composable
fun placeMarkerOnMap(location: LatLng, title: String) {
    Marker(
        state = MarkerState(position = location),
        title = title,
        snippet = "Marker at $title"
    )
}

@Composable
fun GMap(
    currentLocation: MutableState<LatLng?>,
    searchedLocation: MutableState<LatLng?>
) {
    val cameraPositionState = rememberCameraPositionState().apply {
        val initialLocation: LatLng = if(searchedLocation.value == null) {
            currentLocation.value ?: LatLng(0.0, 0.0) // Default to (0,0) if currentLocation is null
        } else {
            searchedLocation.value!!
        }
        position = CameraPosition.fromLatLngZoom(initialLocation, 15f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        // Marker for current location
        currentLocation.value?.let {
            placeMarkerOnMap(location = currentLocation.value!! , title = "Current Location")
        }

        // Marker for searched location
        searchedLocation.value?.let {
            placeMarkerOnMap(location = searchedLocation.value!!, title = "Searched Location")
        }
    }
}
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

    // Effect to update suggestions when search query changes
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
                    // Handle the error.
                    println("Autocomplete prediction fetch failed: ${task.exception?.message}")
                }
            }
        }
    }

    // Display search bar and suggestions if showSuggestions is true
    Column {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = searchQuery.value,
            onValueChange = {
                searchQuery.value = it
                showSuggestions.value = true  // Show suggestions only when user is typing
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
                                searchQuery.value = prediction.getFullText(null).toString() // Update the search bar text
                                showSuggestions.value = false  // Ensure it remains hidden after the update
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


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainInterface() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val currentLocation = remember { mutableStateOf<LatLng?>(null) }
    val searchedLocation = remember { mutableStateOf<LatLng?>(null) }
    val apiKey = "AIzaSyBcDs0jQqyNyk9d1gSpk0ruLgvbd9pwZrU"
    Places.initialize(context, apiKey)
    val placesClient = Places.createClient(context)
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }


    RequestLocationPermission(
        onPermissionGranted = {
            //the dubbger reaches this point
            getCurrentLocation(fusedLocationClient) { location ->
                val latLng = LatLng(location.latitude, location.longitude)
                currentLocation.value = latLng
                if (searchedLocation.value == null) {
                    searchedLocation.value = latLng // Set default camera position
                }
            }
        },
        onPermissionDenied = {
            // Handle permission denial logic, e.g., show a message to the user
            println("Permission denied")
        }
    )
    println("currentLocation: ${currentLocation.value}")
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar for updating the searched location
            LocationSearchBar(
                placesClient = placesClient,
                searchedLocation = searchedLocation
            )


            // Display map with current and searched locations
            GMap(
                currentLocation = currentLocation,
                searchedLocation = searchedLocation
            )

//            searchedLocation.value?.let {
//                Button(onClick = {
//                    val intent = Intent(context, MapsActivity::class.java).apply {
//                        putExtra("currentLocation", currentLocation.value)
//                        putExtra("searchedLocation", searchedLocation.value)
//                    }
//                    context.startActivity(intent)
//                }) {
//                    Text("Show Route")
//                }
//            }

            NavigationHost(navController = navController )
        }
    }

}


