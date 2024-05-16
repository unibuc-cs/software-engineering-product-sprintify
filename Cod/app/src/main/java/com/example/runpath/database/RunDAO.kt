package com.example.runpath.database

import androidx.compose.animation.core.snap
import com.example.runpath.models.Run
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import java.time.Instant
import kotlin.time.Duration

class RunDAO{
    private val db = FirebaseFirestore.getInstance()

    fun insertRun(run: Run, onComplete: (Run) -> Unit) {
        val documentReference = db.collection("runs").document()
        val runId = documentReference.id
        val newRun = run.copy(runID = runId)

        documentReference.set(newRun)
            .addOnSuccessListener {
                onComplete(newRun)
            }
            .addOnFailureListener { e ->
                println("Error adding document: $e")
            }
    }

    fun getRunById(runId: String) {
        db.collection("runs")
            .document(runId)
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

    fun getRuns(onComplete: (List<Run>) -> Unit){
        db.collection("runs")
            .get()
            .addOnSuccessListener { documents ->
                val runs = documents.map { it.toObject<Run>().copy(runID = it.id) }
                onComplete(runs)
            }
            .addOnFailureListener { e ->
                println("Error getting documents: $e")
            }
    }

    fun listenForRuns(onRunsUpdated: (List<Run>) -> Unit): ListenerRegistration {
        return db.collection("runs")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    println("Listen failed: $e")
                    return@addSnapshotListener
                }

                if(snapshots != null){
                    val runs = snapshots.map { it.toObject<Run>().copy(runID = it.id) }
                    onRunsUpdated(runs)
                }
            }
    }

    fun updateRun(
        runID: String,
        userID: String,
        circuitID: String,
        startTime: Instant,
        endTime: Instant,
        pauseTime: Duration,
        timeTracker: Duration,
        paceTracker: Double,
        distanceTracker: Double
    ){
        val run = Run(
            runID = runID,
            userID = userID,
            circuitID = circuitID,
            startTime = startTime,
            endTime = endTime,
            pauseTime = pauseTime,
            timeTracker = timeTracker,
            paceTracker = paceTracker,
            distanceTracker = distanceTracker
        )

        db.collection("runs")
            .document(runID)
            .set(run)
            .addOnSuccessListener {
                println("DocumentSnapshot successfully written!")
            }
            .addOnFailureListener{
                println("Error updating document: $it")
            }
    }

    fun deleteRun(runId: String){
        db.collection("runs")
            .document(runId)
            .delete()
            .addOnSuccessListener {
                println("DocumentSnapshot successfully deleted!")
            }
            .addOnFailureListener { e ->
                println("Error deleting document: $e")
            }
    }
}