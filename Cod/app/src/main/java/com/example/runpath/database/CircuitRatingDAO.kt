package com.example.runpath.database

import com.example.runpath.models.CircuitRating
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject

class CircuitRatingDAO {
    private val db = FirebaseFirestore.getInstance()

    fun insertCircuitRating(circuitRating: CircuitRating, onComplete: (CircuitRating) -> Unit) {
        val documentReference = db.collection("circuitRatings").document()
        val circuitRatingId = documentReference.id
        val newCircuitRating = circuitRating.copy(circuitRatingId = circuitRatingId)

        documentReference.set(newCircuitRating)
            .addOnSuccessListener {
                onComplete(newCircuitRating)
            }
            .addOnFailureListener { e ->
                println("Error adding document: $e")
            }
    }

    fun getCircuitRatingById(circuitRatingId: String) {
        db.collection("circuitRatings")
            .document(circuitRatingId)
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

    fun getCircuitRatings(onComplete: (List<CircuitRating>) -> Unit){
        db.collection("circuitRatings")
            .get()
            .addOnSuccessListener { documents ->
                val circuitRatings = documents.map { it.toObject<CircuitRating>().copy(circuitRatingId = it.id) }
                onComplete(circuitRatings)
            }
            .addOnFailureListener { e ->
                println("Error getting documents: $e")
            }
    }

    fun listenForCircuitRatings(onCircuitRatingsUpdated: (List<CircuitRating>) -> Unit): ListenerRegistration {
        return db.collection("circuitRatings")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    println("Listen failed: $e")
                    return@addSnapshotListener
                }
                if(snapshots != null){
                    val circuitRatings = snapshots.map { it.toObject<CircuitRating>().copy(circuitRatingId = it.id) }
                    onCircuitRatingsUpdated(circuitRatings)
                }
            }
    }

    fun updateCircuitRating(
        circuitRatingId: String,
        circuitId: String,
        userId: String,
        rating: Double,
        petFriendly: Boolean,
        lightLevel: Int
    ) {
        val circuitRating = CircuitRating(
            circuitRatingId = circuitRatingId,
            circuitId = circuitId,
            userId = userId,
            rating = rating,
            petFriendly = petFriendly,
            lightLevel = lightLevel
        )
        db.collection("circuitRatings")
            .document(circuitRatingId)
            .set(circuitRating)
            .addOnSuccessListener {
                println("DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e ->
                println("Error updating document: $e")
            }
    }

    fun deleteCircuitRating(circuitRatingId: String) {
        db.collection("circuitRatings")
            .document(circuitRatingId)
            .delete()
            .addOnSuccessListener {
                println("DocumentSnapshot successfully deleted!")
            }
            .addOnFailureListener { e ->
                println("Error deleting document: $e")
            }
    }
}