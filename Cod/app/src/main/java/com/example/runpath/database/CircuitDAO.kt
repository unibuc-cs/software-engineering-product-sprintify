package com.example.runpath.database

import androidx.compose.animation.core.snap
import com.example.runpath.models.Circuit
import com.example.runpath.others.MyLatLng

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import com.google.maps.model.LatLng


class CircuitDAO {
    // creez o noua instanta a bazei de date
    private val db = FirebaseFirestore.getInstance()
    // creez un nou circuit
    fun insertCircuit(circuit: Circuit, onComplete: (Circuit) -> Unit) {
        val documentReference = db.collection("circuits").document()
        val circuitId = documentReference.id
        val newCircuit = circuit.copy(circuitId = circuitId)

        documentReference.set(newCircuit)
            .addOnSuccessListener {
                onComplete(newCircuit)
            }
            .addOnFailureListener { e ->
                println("Error adding document: $e")
            }
    }
    // obtin un circuit dupa id
    fun getCircuitbyId(circuitId: String) {
        db.collection("circuits")
            .document(circuitId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    println("${document.id} => ${document.data}")
                } else {
                    println("No such document")
                }
            }
            .addOnFailureListener { exception ->
                println("Error getting document: $exception")
            }
    }
    // obtin toate circuitele
    fun getCircuits(onComplete: (List<Circuit>) -> Unit){
        db.collection("circuits")
            .get()
            .addOnSuccessListener { documents ->
                val circuits = documents.map { it.toObject<Circuit>().copy(circuitId = it.id) }
                onComplete(circuits)
            }
            .addOnFailureListener { e ->
                println("Error getting documents: $e")
            }
    }

    fun listenForCircuits(onCircuitsUpdated: (List<Circuit>) -> Unit): ListenerRegistration {
        return db.collection("circuits")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    println("Listen failed: $e")
                    return@addSnapshotListener
                }
                if(snapshots != null){
                    val circuits = snapshots.map { it.toObject<Circuit>().copy(circuitId = it.id) }
                    onCircuitsUpdated(circuits)
                }
            }
    }
    // actualizez un circuit
    fun updateCircuit(
        circuitId: String,
        name: String,
        description: String,
        distance: Double,
        estimatedTime: String,
        intensity: Int,
        terrain: String,
        petFriendly: Boolean,
        lightLevel: Int,
        rating: Double,
        difficulty: Int,
        route : List<LatLng>
    ) {
        val circuit = Circuit(
            circuitId = circuitId,
            name = name,
            description = description,
            distance = distance,
            estimatedTime = estimatedTime,
            intensity = intensity,
            terrain = terrain,
            petFriendly = petFriendly,
            lightLevel = lightLevel,
            rating = rating,
            difficulty = difficulty,
            route = route.map { toMyLatLng(it) }
        )
        db.collection("circuits")
            .document(circuitId)
            .set(circuit)
            .addOnSuccessListener {
                println("DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e ->
                println("Error updating document: $e")
            }
    }
    // sterg un circuit
    fun deleteCircuit(circuitId: String) {
        db.collection("circuits")
            .document(circuitId)
            .delete()
            .addOnSuccessListener {
                println("DocumentSnapshot successfully deleted!")
            }
            .addOnFailureListener { e ->
                println("Error deleting document: $e")
            }
    }
    fun toMyLatLng(latLng: LatLng): MyLatLng{
        return MyLatLng(latLng.lat, latLng.lng)
    }
}