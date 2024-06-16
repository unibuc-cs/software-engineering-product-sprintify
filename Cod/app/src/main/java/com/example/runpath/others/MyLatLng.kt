package com.example.runpath.others


// MyLatLng made to mimic the LatLng class from Google Maps
//used because of the error when using the LatLng class from Google Maps and some mixups
data class MyLatLng(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
)