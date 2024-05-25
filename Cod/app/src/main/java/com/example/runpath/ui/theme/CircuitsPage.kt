package com.example.runpath.ui.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.runpath.database.CircuitDAO
import com.example.runpath.database.SessionManager
import com.example.runpath.models.Circuit
import com.google.maps.model.LatLng


@Composable
fun CircuitsPage(navController: NavController,sessionManager: SessionManager) {

    val circuitDao = CircuitDAO()
    var circuits by remember { mutableStateOf(listOf<Circuit>()) }

    //polyline for the route



}