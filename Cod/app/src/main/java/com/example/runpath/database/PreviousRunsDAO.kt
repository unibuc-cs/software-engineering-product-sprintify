package com.example.runpath.database

import android.content.Context
import com.example.runpath.models.PreviousRuns
import com.google.firebase.firestore.FirebaseFirestore

class PreviousRunsDAO(context: Context) {
    private val db = FirebaseFirestore.getInstance()

    // Insert a new run
    fun insertRun(
            run: PreviousRuns,
            runId: String,
            onComplete: (PreviousRuns) -> Unit
    ) {
        val documentReference = db.collection("previousRuns").document(runId)
        val newRun = run.copy(runId = runId)

        documentReference.set(newRun)
                .addOnSuccessListener {
            onComplete(newRun)
        }
            .addOnFailureListener { e ->
                println("Error adding document: $e")
        }
    }

    // Retrieve a run by ID
    fun getRunById(runId: String, onComplete: (PreviousRuns) -> Unit) {
        db.collection("previousRuns")
                .document(runId)
                .get()
                .addOnSuccessListener { document ->
            if (document.exists()) {
                val run = PreviousRuns(
                        runId = document.id,
                        userId = document.getString("userId"),
                        circuitId = document.getString("circuitId"),
                        startTime = document.getString("startTime"),
                        endTime = document.getString("endTime"),
                        pauseTime = document.getString("pauseTime"),
                        timeTracker = document.getString("timeTracker"),
                        paceTracker = document.getString("paceTracker"),
                        distanceTracker = document.getString("distanceTracker")
                )
                onComplete(run)
            } else {
                println("No such document")
            }
        }
    }

    // Update a run
    fun updateRun(
            runId: String,
            userId: String?,
            circuitId: String?,
            startTime: String?,
            endTime: String?,
            pauseTime: String?,
            timeTracker: String?,
            paceTracker: String?,
            distanceTracker: String?
    ) {
        val updatedRun = PreviousRuns(
                runId = runId,
                userId = userId,
                circuitId = circuitId,
                startTime = startTime,
                endTime = endTime,
                pauseTime = pauseTime,
                timeTracker = timeTracker,
                paceTracker = paceTracker,
                distanceTracker = distanceTracker
        )
        db.collection("previousRuns").document(runId)
                .set(updatedRun)
                .addOnSuccessListener {
            println("DocumentSnapshot successfully written!")
        }
            .addOnFailureListener { e ->
                println("Error adding document: $e")
        }
    }

    // Delete a run
    fun deleteRun(runId: String) {
        db.collection("previousRuns").document(runId)
                .delete()
                .addOnSuccessListener {
            println("DocumentSnapshot successfully deleted!")
        }
            .addOnFailureListener { e ->
                println("Error deleting document: $e")
        }
    }
}
