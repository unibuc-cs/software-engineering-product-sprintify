package com.example.runpath.others

import com.example.runpath.models.Circuit
import com.google.android.gms.maps.model.LatLng
import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson

object SerializationUtil {
    // creez un nou obiect moshi
    private val moshi = Moshi.Builder()
        .add(LatLngAdapter())
        .build()
    // creez un nou adaptor pentru circuit
    private val circuitAdapter = moshi.adapter(Circuit::class.java)

    fun serializeCircuit(circuit: Circuit): String {
        return circuitAdapter.toJson(circuit)
    }

    fun deserializeCircuit(circuitString: String): Circuit? {
        return circuitAdapter.fromJson(circuitString)
    }
}
// creez un nou adaptor pentru LatLng
class LatLngAdapter {
    @ToJson
    fun toJson(latLng: LatLng): Map<String, Double> {
        return mapOf("lat" to latLng.latitude, "lng" to latLng.longitude)
    }

    @FromJson
    fun fromJson(json: Map<String, Double>): LatLng {
        return LatLng(json["lat"] ?: 0.0, json["lng"] ?: 0.0)
    }
}