package com.example.runpath.database

import androidx.compose.animation.core.snap
import com.example.runpath.models.Circuit
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject


class CircuitDAO {
    private val db = FirebaseFirestore.getInstance()

    fun insertCircuit(circuit: Circuit, onComplete: (Circuit) -> Unit) {
        val documentReference = db.collection("circuits").document()
        val circuitId = documentReference.id
        val newCircuit = circuit.copy(circuitID = circuitId)

        documentReference.set(newCircuit)
            .addOnSuccessListener {
                onComplete(newCircuit)
            }
            .addOnFailureListener { e ->
                println("Error adding document: $e")
            }
    }

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

    fun getCircuits(onComplete: (List<Circuit>) -> Unit){
        db.collection("circuits")
            .get()
            .addOnSuccessListener { documents ->
                val circuits = documents.map { it.toObject<Circuit>().copy(circuitID = it.id) }
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
                    val circuits = snapshots.map { it.toObject<Circuit>().copy(circuitID = it.id) }
                    onCircuitsUpdated(circuits)
                }
            }
    }

    fun updateCircuit(
        circuitId: String,
        name: String,
        description: String,
        distance: Double,
        estimatedTime: String,
        intensity: Int,
        terrain: String,
        petFriendly: Boolean,
        lightlevel: Int,
        rating: Double,
        difficulty: Int
    ) {
        val circuit = Circuit(
            circuitID = circuitId,
            name = name,
            description = description,
            distance = distance,
            estimatedTime = estimatedTime,
            intensity = intensity,
            terrain = terrain,
            petFriendly = petFriendly,
            lightlevel = lightlevel,
            rating = rating,
            difficulty = difficulty
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
}