package com.example.runpath.tracking

import android.annotation.SuppressLint
import android.app.Application
import android.os.Looper
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

//data class Segment(val startIndex: Int, val color: Color)

class LocationViewModel(application: Application) : AndroidViewModel(application){

    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(application)

    private val _pathPoints = MutableStateFlow<List<LatLng>>(emptyList())
    val pathPoints: StateFlow<List<LatLng>> = _pathPoints;

//    private val _segments = MutableStateFlow<List<Segment>>(emptyList())
//    val segments: StateFlow<List<Segment>> = _segments

    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking

    private val locationCallback = object: LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            if(_isTracking.value) {
                locationResult.locations.forEach {location ->
                    val newPoint = LatLng(location.latitude, location.longitude)
                    _pathPoints.value += newPoint
                }
            }
        }
    }

    private val locationRequest = LocationRequest.create().apply {
        interval = 5000 // 5 second
        fastestInterval = 2000  // 2 seconds
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    @SuppressLint("MissingPermission")
    fun startTracking() {
        _isTracking.value = true
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    fun pauseTracking() {
        _isTracking.value = false
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onCleared() {
        super.onCleared()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}