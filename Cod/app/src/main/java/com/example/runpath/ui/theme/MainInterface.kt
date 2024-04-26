package com.example.runpath.ui.theme

import android.annotation.SuppressLint
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
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
        composable(BottomNavItem.Profile.route) { /* Profile Screen UI */ }
    }
}

@Composable
fun GMap() {
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
            GMap()
            NavigationHost(navController = navController)
        }
    }
}


