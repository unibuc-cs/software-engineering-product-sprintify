package com.example.runpath.ui.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.runpath.database.CircuitDAO
import com.example.runpath.database.SessionManager
import com.example.runpath.models.Circuit
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.model.LatLng


@Composable
fun CircuitsPage(navController: NavController,sessionManager: SessionManager) {

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
                Row {
                    Text(
                        text = circuit.name,
                        modifier = Modifier.padding(16.dp)
                    )
                    val route = circuit.route
                    if (route != null) {
                        // Display the route
                        val polylineOptions = PolylineOptions()
                        route.forEach { latLng ->
                            polylineOptions.add(com.google.android.gms.maps.model.LatLng(latLng.lat, latLng.lng))

                        }

                        //googleMap.addPolyline(polylineOptions)


                    }
                }
            }
        }
    }





}