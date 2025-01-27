package com.example.runpath.database

import android.util.Log
import com.example.runpath.models.CircuitRating
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject

class CircuitRatingDAO {
    private val db = FirebaseFirestore.getInstance()
    private val circuitDAO = CircuitDAO()

    // region Rating Operations
    fun saveCircuitRating(
        circuitId: String,
        userId: String,
        intensity: Int,
        lightLevel: Int,
        difficulty: Int,
        onComplete: (String) -> Unit, // Returns the new rating ID
        onError: (Exception) -> Unit
    ) {
        db.collection("circuitRatings")
            .whereEqualTo("circuitId", circuitId)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    insertCircuitRating(
                        CircuitRating(
                            circuitId = circuitId,
                            userId = userId,
                            intensity = intensity,
                            lightLevel = lightLevel,
                            difficulty = difficulty
                        ),
                        onSuccess = { newId ->
                            circuitDAO.updateCircuitWithMedianRatings(circuitId)
                            onComplete(newId)
                        },
                        onError = onError
                    )
                } else {
                    val docId = documents.documents.first().id
                    updateExistingRating(
                        ratingId = docId,
                        circuitId = circuitId,
                        userId = userId,
                        intensity = intensity,
                        lightLevel = lightLevel,
                        difficulty = difficulty,
                        onSuccess = { ratingId ->
                            circuitDAO.updateCircuitWithMedianRatings(circuitId)
                            onComplete(ratingId)
                        },
                        onError = onError
                    )
                }
            }
            .addOnFailureListener(onError)
    }

    fun getCircuitRatingsByCircuit(circuitId: String, onComplete: (List<CircuitRating>) -> Unit) {
        db.collection("circuitRatings")
            .whereEqualTo("circuitId", circuitId)
            .get()
            .addOnSuccessListener { documents ->
                val ratings = documents.map {
                    it.toObject<CircuitRating>().copy(circuitRatingId = it.id)
                }
                onComplete(ratings)
            }
            .addOnFailureListener { e ->
                Log.e("CircuitRatingDAO", "Error getting ratings", e)
            }
    }

    fun listenForCircuitRatings(
        circuitId: String,
        onRatingsUpdated: (List<CircuitRating>) -> Unit
    ): ListenerRegistration {
        return db.collection("circuitRatings")
            .whereEqualTo("circuitId", circuitId)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.e("CircuitRatingDAO", "Listen failed", e)
                    return@addSnapshotListener
                }
                val ratings = snapshots?.map {
                    it.toObject<CircuitRating>().copy(circuitRatingId = it.id)
                } ?: emptyList()
                onRatingsUpdated(ratings)
            }
    }
    // endregion

    // region Private Helpers
     fun insertCircuitRating(
        circuitRating: CircuitRating,
        onSuccess: (String) -> Unit, // Return the new ID
        onError: (Exception) -> Unit
    ) {
        val docRef = db.collection("circuitRatings").document()
        val newRating = circuitRating.copy(circuitRatingId = docRef.id)

        docRef.set(newRating)
            .addOnSuccessListener {
                onSuccess(docRef.id)
            }
            .addOnFailureListener(onError)
    }

    private fun updateExistingRating(
        ratingId: String,
        circuitId: String,
        userId: String,
        intensity: Int,
        lightLevel: Int,
        difficulty: Int,
        onSuccess: (String) -> Unit,  // Changed from () -> Unit
        onError: (Exception) -> Unit
    ) {
        db.collection("circuitRatings")
            .document(ratingId)
            .update(
                mapOf(
                    "intensity" to intensity,
                    "lightLevel" to lightLevel,
                    "difficulty" to difficulty,
                    "timestamp" to System.currentTimeMillis()
                )
            )
            .addOnSuccessListener {
                onSuccess(ratingId)  // Pass the rating ID back
            }
            .addOnFailureListener(onError)
    }
    // endregion
}