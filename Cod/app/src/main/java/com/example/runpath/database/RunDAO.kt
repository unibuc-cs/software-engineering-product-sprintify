package com.example.runpath.database

import androidx.compose.animation.core.snap
import com.example.runpath.models.Run
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import java.time.Instant
import kotlin.time.Duration

class RunDAO{
    // creez o noua instanta a bazei de date
    private val db = FirebaseFirestore.getInstance()
    // creez un nou run
    fun insertRun(run: Run, onComplete: (Run) -> Unit) {
        val documentReference = db.collection("runs").document()
        val runId = documentReference.id
        val newRun = run.copy(runId = runId)

        documentReference.set(newRun)
            .addOnSuccessListener {
                onComplete(newRun)
            }
            .addOnFailureListener { e ->
                println("Error adding document: $e")
            }
    }
    // obtin un run dupa id
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
    // obtin toate runurile
    fun getRuns(onComplete: (List<Run>) -> Unit){
        db.collection("runs")
            .get()
            .addOnSuccessListener { documents ->
                val runs = documents.map { it.toObject<Run>().copy(runId = it.id) }
                onComplete(runs)
            }
            .addOnFailureListener { e ->
                println("Error getting documents: $e")
            }
    }
    // creez un listener pentru runuri
    fun listenForRuns(onRunsUpdated: (List<Run>) -> Unit): ListenerRegistration {
        return db.collection("runs")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    println("Listen failed: $e")
                    return@addSnapshotListener
                }

                if(snapshots != null){
                    val runs = snapshots.map { it.toObject<Run>().copy(runId = it.id) }
                    onRunsUpdated(runs)
                }
            }
    }
    // actualizez un run
    fun updateRun(
        runId: String,
        userId: String,
        circuitId: String,
        startTime: Instant,
        endTime: Instant,
        pauseTime: Duration,
        timeTracker: Duration,
        paceTracker: Double,
        distanceTracker: Double
    ){
        val run = Run(
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

        db.collection("runs")
            .document(runId)
            .set(run)
            .addOnSuccessListener {
                println("DocumentSnapshot successfully written!")
            }
            .addOnFailureListener{
                println("Error updating document: $it")
            }
    }
    // sterg un run
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