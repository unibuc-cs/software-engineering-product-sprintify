package com.example.runpath.database

import androidx.compose.animation.core.snap
import com.example.runpath.models.Community
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject

class CommunityDAO{
    private val db = FirebaseFirestore.getInstance()

    fun insertCommunity(community: Community, onComplete: (Community) -> Unit) {
        val documentReference = db.collection("communities").document()
        val communityId = documentReference.id
        val newCommunity = community.copy(communityID = communityId)

        documentReference.set(newCommunity)
            .addOnSuccessListener {
                onComplete(newCommunity)
            }
            .addOnFailureListener { e ->
                println("Error adding document: $e")
            }
    }

    fun getCommunityById(communityId: String){
        db.collection("communities")
            .document(communityId)
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

    fun getCommunities(onComplete: (List<Community>) -> Unit){
        db.collection("communities")
            .get()
            .addOnSuccessListener { documents ->
                val communities = documents.map { it.toObject<Community>().copy(communityID = it.id) }
                onComplete(communities)
            }
            .addOnFailureListener { e ->
                println("Error getting documents: $e")
            }
    }

    fun listenForCommunities(onCommunitiesUpdated: (List<Community>) -> Unit): ListenerRegistration {
        return db.collection("communities")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    println("Listen failed: $e")
                    return@addSnapshotListener
                }
                if(snapshots != null){
                    val communities = snapshots.map { it.toObject<Community>().copy(communityID = it.id) }
                    onCommunitiesUpdated(communities)
                }
            }
    }

    fun updateCommunity(
        communityID: String,
        name: String,
        description: String
    ){
        val community= Community(
            communityID = communityID,
            name = name,
            description = description
        )

        db.collection("communities")
            .document(communityID)
            .set(community)
            .addOnSuccessListener {
                println("DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e ->
                println("Error updating document: $e")
            }
    }

    fun deleteCommunity(communityID: String){
        db.collection("communities")
            .document(communityID)
            .delete()
            .addOnSuccessListener {
                println("DocumentSnapshot successfully deleted!")
            }
            .addOnFailureListener { e ->
                println("Error deleting document: $e")
            }
    }
}
