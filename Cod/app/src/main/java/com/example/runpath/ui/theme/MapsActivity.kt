package com.example.runpath.ui.theme

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.example.runpath.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode

class MapsActivity : FragmentActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var currentLocation: LatLng
    private lateinit var searchedLocation: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Get the current location and searched location from the intent extras
        val currentLocationBundle = intent.getBundleExtra("currentLocation")
        val searchedLocationBundle = intent.getBundleExtra("searchedLocation")

        if (currentLocationBundle != null) {
            currentLocation = LatLng(
                currentLocationBundle.getDouble("latitude"),
                currentLocationBundle.getDouble("longitude")
            )
        }

        if (searchedLocationBundle != null) {
            searchedLocation = LatLng(
                searchedLocationBundle.getDouble("latitude"),
                searchedLocationBundle.getDouble("longitude")
            )
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    fun getRoutePoints(point1: LatLng, point2: LatLng): List<LatLng> {
        val context = GeoApiContext.Builder()
            .apiKey("AIzaSyBcDs0jQqyNyk9d1gSpk0ruLgvbd9pwZrU")
            .build()

        val result: DirectionsResult = DirectionsApi.newRequest(context)
            .mode(TravelMode.WALKING)
            .origin(com.google.maps.model.LatLng(point1.latitude, point1.longitude))
            .destination(com.google.maps.model.LatLng(point2.latitude, point2.longitude))
            .await()

        return result.routes[0].overviewPolyline.decodePath().map {
            LatLng(it.lat, it.lng)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Draw a polyline between the current location and the searched location
        drawPolyline(mMap, currentLocation, searchedLocation)
    }

    fun drawPolyline(map: GoogleMap, point1: LatLng, point2: LatLng) {
        val context = GeoApiContext.Builder()
            .apiKey("AIzaSyBcDs0jQqyNyk9d1gSpk0ruLgvbd9pwZrU")
            .build()

        val result: DirectionsResult = DirectionsApi.newRequest(context)
            .mode(TravelMode.WALKING)
            .origin(com.google.maps.model.LatLng(point1.latitude, point1.longitude))
            .destination(com.google.maps.model.LatLng(point2.latitude, point2.longitude))
            .await()

        val path: List<LatLng> = result.routes[0].overviewPolyline.decodePath().map {
            LatLng(it.lat, it.lng)
        }

        map.addPolyline(PolylineOptions().addAll(path))
    }
}