package com.example.runpath.database

import android.util.Log
import com.example.runpath.models.Circuit
import com.example.runpath.models.CircuitRating
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject

class CircuitDAO {
    private val db = FirebaseFirestore.getInstance()

    // region Circuit Operations
    fun insertCircuit(circuit: Circuit, onComplete: (Circuit) -> Unit) {
        val documentReference = db.collection("circuits").document()
        val circuitId = documentReference.id
        val newCircuit = circuit.copy(circuitId = circuitId)

        documentReference.set(newCircuit)
            .addOnSuccessListener { onComplete(newCircuit) }
            .addOnFailureListener { e -> Log.e("CircuitDAO", "Error adding circuit", e) }
    }

    fun getCircuits(onComplete: (List<Circuit>) -> Unit) {
        db.collection("circuits")
            .get()
            .addOnSuccessListener { documents ->
                val circuits = documents.map { it.toObject<Circuit>().copy(circuitId = it.id) }
                onComplete(circuits)
            }
            .addOnFailureListener { e -> Log.e("CircuitDAO", "Error getting circuits", e) }
    }

    // In CircuitDAO.kt
    fun listenForCircuits(onCircuitsUpdated: (List<Circuit>) -> Unit): ListenerRegistration {
        return db.collection("circuits")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("CircuitDAO", "Listen failed", e)
                    return@addSnapshotListener
                }

                val circuits = snapshots?.documents?.mapNotNull {
                    try {
                        it.toObject(Circuit::class.java)?.copy(circuitId = it.id)
                    } catch (ex: Exception) {
                        Log.e("CircuitDAO", "Error deserializing circuit", ex)
                        null
                    }
                } ?: emptyList()

                onCircuitsUpdated(circuits)
            }
    }

    fun updateCircuitWithMedianRatings(circuitId: String) {
        FirebaseFirestore.getInstance()
            .collection("circuitRatings")
            .whereEqualTo("circuitId", circuitId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val ratings = querySnapshot.documents.mapNotNull {
                    it.toObject(CircuitRating::class.java)
                }

                val updates = mapOf(
                    "intensity" to calculateMedian(ratings.map { it.intensity }),
                    "lightLevel" to calculateMedian(ratings.map { it.lightLevel }),
                    "difficulty" to calculateMedian(ratings.map { it.difficulty })
                )

                db.collection("circuits")
                    .document(circuitId)
                    .update(updates)
                    .addOnFailureListener { e ->
                        Log.e("CircuitDAO", "Error updating circuit ratings", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("CircuitDAO", "Error getting ratings", e)
            }
    }
    // endregion

    // region Helpers
    private fun calculateMedian(values: List<Int>): Int {
        if (values.isEmpty()) return 0
        val sorted = values.sorted()
        return if (sorted.size % 2 == 0) {
            (sorted[sorted.size/2 - 1] + sorted[sorted.size/2]) / 2
        } else {
            sorted[sorted.size/2]
        }
    }
    // endregion
}