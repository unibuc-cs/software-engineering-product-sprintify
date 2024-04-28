package com.example.runpath.ui.theme

import android.annotation.SuppressLint
import android.content.Context
import android.health.connect.datatypes.ExerciseRoute
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.Scaffold
import androidx.compose.material.Icon
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import android.location.Geocoder
import android.location.Address
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext



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
        composable(BottomNavItem.Profile.route) { /* Profile Screen UI */ }
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
            if(granted) {
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
        if(location != null) {
            onLocationReceived(location)
        }
    }
}


@Composable
fun GMapWithCurrentLocation() {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context)}

    val currentLocation = remember { mutableStateOf<LatLng?>(null)}
    val cameraPositionState = rememberCameraPositionState()

    var showDialog by remember { mutableStateOf(false)}

    // Request location permission
    RequestLocationPermission(
        onPermissionGranted = {
            // Get current location when permission is granted
            getCurrentLocation(fusedLocationClient) {location ->
                val latLng = LatLng(location.latitude, location.longitude)
                currentLocation.value = latLng

                // Update the camera position
                cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 15f)
            }
        },
        onPermissionDenied = {
            // Permission denial logic
            showDialog = true
        }
    )

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState  // Set camera position
    ) {
        currentLocation.value?.let {
            Marker(
                state = MarkerState(position = it),
                title = "Current Location",
                snippet = "You are here"
            )
        }
    }

    if(showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text("Location Permission Denied")
            },
            text = {
                Text("This app requires location access to function properly. Please grant location permission in your device settings.")
            },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}


@Composable
fun GMap() {   // momentarily unused
    val singapore = LatLng(44.86, 13.84)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(singapore, 10f)
    }
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = MarkerState(position = singapore),
            title = "Pula",
            snippet = "Marker in Pula"
        )
    }
}

@Composable
fun LocationSearchBar(cameraPositionState: MutableState<CameraPosition>) {
    val context = LocalContext.current
    val geocoder = remember { Geocoder(context) }
    var searchQuery by remember { mutableStateOf("") }

    TextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        label = { Text("Search location") },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            val addresses: List<Address> = geocoder.getFromLocationName(searchQuery, 1) ?: listOf()
            if (addresses.isNotEmpty()) {
                val location = LatLng(addresses[0].latitude, addresses[0].longitude)
                cameraPositionState.value = CameraPosition.fromLatLngZoom(location, 15f)
            }
        })
    )
}


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainInterface() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) {paddingValues ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            GMapWithCurrentLocation()   // Display the map
            NavigationHost(navController = navController)
        }
    }

}


