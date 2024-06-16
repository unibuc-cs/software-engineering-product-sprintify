package com.example.runpath.database

import com.example.runpath.models.Community
import com.example.runpath.models.Community_Users
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import java.time.LocalDateTime

class CommunityDAO{
    // creez o noua instanta a bazei de date
    private val db = FirebaseFirestore.getInstance()
    // creez o noua comunitate
    fun insertCommunity(community: Community, onComplete: (Community) -> Unit) {
        val documentReference = db.collection("communities").document()
        val communityId = documentReference.id
        val newCommunity = community.copy(communityId = communityId)

        documentReference.set(newCommunity)
            .addOnSuccessListener {
                onComplete(newCommunity)
            }
            .addOnFailureListener { e ->
                println("Error adding document: $e")
            }
    }
    // obtin o comunitate dupa id
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
    // obtin toate comunitatile
    fun getCommunities(onComplete: (List<Community>) -> Unit){
        db.collection("communities")
            .get()
            .addOnSuccessListener { documents ->
                val communities = documents.map { it.toObject<Community>().copy(communityId = it.id) }
                onComplete(communities)
            }
            .addOnFailureListener { e ->
                println("Error getting documents: $e")
            }
    }
    // creez un listener pentru comunitati
    fun listenForCommunities(onCommunitiesUpdated: (List<Community>) -> Unit): ListenerRegistration {
        return db.collection("communities")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    println("Listen failed: $e")
                    return@addSnapshotListener
                }
                if(snapshots != null){
                    val communities = snapshots.map { it.toObject<Community>().copy(communityId = it.id) }
                    onCommunitiesUpdated(communities)
                }
            }
    }
    // actualizez o comunitate
    fun updateCommunity(
        communityId: String,
        name: String,
        description: String
    ){
        val community= Community(
            communityId = communityId,
            name = name,
            description = description
        )

        db.collection("communities")
            .document(communityId)
            .set(community)
            .addOnSuccessListener {
                println("DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e ->
                println("Error updating document: $e")
            }
    }
    // sterg o comunitate
    fun deleteCommunity(communityId: String){
        db.collection("communities")
            .document(communityId)
            .delete()
            .addOnSuccessListener {
                println("DocumentSnapshot successfully deleted!")
            }
            .addOnFailureListener { e ->
                println("Error deleting document: $e")
            }
    }

    fun joinCommunity(communityId: String, userId: String) {
        val community_users = Community_Users(
            communityId = communityId,
            userId = userId,
            dateJoined = LocalDateTime.now().toString()
        )

        db.collection("community_users")
            .add(community_users)
            .addOnSuccessListener {
                println("DocumentSnapshot successfully added!")
            }
            .addOnFailureListener { e ->
                println("Error adding community: $e")
            }
    }
}
