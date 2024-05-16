package com.example.runpath.database

import androidx.compose.animation.core.snap
import com.example.runpath.models.SavedCircuits
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject

class SavedCircuitsDAO{
    private val db = FirebaseFirestore.getInstance()

    fun insertSavedCircuit(savedCircuit: SavedCircuits, onComplete: (SavedCircuits) -> Unit) {
        val documentReference = db.collection("savedCircuits").document()
        val savedCircuitId = documentReference.id
        val newSavedCircuit = savedCircuit.copy(savedCircuitID = savedCircuitId)

        documentReference.set(newSavedCircuit)
            .addOnSuccessListener {
                onComplete(newSavedCircuit)
            }
            .addOnFailureListener { e ->
                println("Error adding document: $e")
            }
    }

    fun getSavedCircuitById(savedCircuitId: String) {
        db.collection("savedCircuits")
            .document(savedCircuitId)
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

    fun getSavedCircuits(onComplete: (List<SavedCircuits>) -> Unit){
        db.collection("savedCircuits")
            .get()
            .addOnSuccessListener { documents ->
                val savedCircuits = documents.map { it.toObject<SavedCircuits>().copy(savedCircuitID = it.id) }
                onComplete(savedCircuits)
            }
            .addOnFailureListener { e ->
                println("Error getting documents: $e")
            }
    }

    fun listenForSavedCircuits(onSavedCircuitsUpdated: (List<SavedCircuits>) -> Unit): ListenerRegistration {
        return db.collection("savedCircuits")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    println("Listen failed: $e")
                    return@addSnapshotListener
                }
            }
    }

    fun updateSavedCircuit(
        savedCircuitID: String,
        circuitID: String,
        userID: String,
    ){
        val savedCircuit = SavedCircuits(
            savedCircuitID = savedCircuitID,
            circuitID = circuitID,
            userID = userID
        )

        db.collection("savedCircuits")
            .document(savedCircuitID)
            .set(savedCircuit)
            .addOnSuccessListener {
                println("DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e ->
                println("Error updating document: $e")
            }
    }

    fun deleteSavedCircuit(savedCircuitID: String){
        db.collection("savedCircuits")
            .document(savedCircuitID)
            .delete()
            .addOnSuccessListener {
                println("DocumentSnapshot successfully deleted!")
            }
            .addOnFailureListener { e ->
                println("Error deleting document: $e")
            }
    }
}