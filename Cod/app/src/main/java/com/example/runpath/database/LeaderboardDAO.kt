package com.example.runpath.database

import androidx.compose.animation.core.snap
import com.example.runpath.models.Leaderboard
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject


class LeaderboardDAO{
    private val db = FirebaseFirestore.getInstance()

    fun insertLeaderboard(leaderboard: Leaderboard, onComplete: (Leaderboard) -> Unit) {
        val documentReference = db.collection("leaderboards").document()
        val leaderboardId = documentReference.id
        val newLeaderboard = leaderboard.copy(leaderboardId = leaderboardId)

        documentReference.set(newLeaderboard)
            .addOnSuccessListener {
                onComplete(newLeaderboard)
            }
            .addOnFailureListener { e ->
                println("Error adding document: $e")
            }
    }

    fun getLeaderboardById(leaderboardId: String) {
        db.collection("leaderboards")
            .document(leaderboardId)
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

    fun getLeaderboards(onComplete: (List<Leaderboard>) -> Unit){
        db.collection("leaderboards")
            .get()
            .addOnSuccessListener { documents ->
                val leaderboards = documents.map { it.toObject<Leaderboard>().copy(leaderboardId = it.id) }
                onComplete(leaderboards)
            }
            .addOnFailureListener { e ->
                println("Error getting documents: $e")
            }
    }

    fun listenForLeaderboards(onLeaderboardsUpdated: (List<Leaderboard>) -> Unit): ListenerRegistration {
        return db.collection("leaderboards")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    println("Listen failed: $e")
                    return@addSnapshotListener
                }
                if(snapshots != null){
                    val leaderboards = snapshots.map { it.toObject<Leaderboard>().copy(leaderboardId = it.id) }
                    onLeaderboardsUpdated(leaderboards)
                }
            }
    }

    fun updateLeaderboard(
        leaderboardId : String,
        circuitId: String,
        userID: String,
        rank: Int,
        time: String
    ){
        val leaderboard = Leaderboard(
            leaderboardId = leaderboardId,
            circuitId = circuitId,
            userID = userID,
            rank = rank,
            time = time
        )

        db.collection("leaderboards")
            .document(leaderboardId)
            .set(leaderboard)
            .addOnSuccessListener {
                println("DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e ->
                println("Error adding document: $e")
            }
    }

    fun deleteLeaderboard(leaderboardId: String){
        db.collection("leaderboards")
            .document(leaderboardId)
            .delete()
            .addOnSuccessListener {
                println("DocumentSnapshot successfully deleted!")
            }
            .addOnFailureListener { e ->
                println("Error deleting document: $e")
            }
    }
}