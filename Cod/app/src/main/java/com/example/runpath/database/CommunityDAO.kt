package com.example.runpath.database

import com.example.runpath.models.Community
import com.example.runpath.models.Community_Users
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime

class CommunityDAO{
    // creez o noua instanta a bazei de date
    private val db = FirebaseFirestore.getInstance()
    // creez o noua comunitate
    fun insertCommunity(community: Community, onComplete: (Community) -> Unit) {
        val documentReference = db.collection("communities").document()
        val communityId = documentReference.id
        val newCommunity = community.copy(communityId = communityId, createdBy = community.createdBy)

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

    fun listenForJoinedCommunities(userId: String, onJoinedCommunitiesUpdated: (List<Community>) -> Unit): ListenerRegistration {
        return db.collection("community_users")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    println("Listen failed: $e")
                    return@addSnapshotListener
                }
                if (snapshots != null) {
                    val joinedCommunities = snapshots.map { it.toObject<Community_Users>().communityId }
                    val communities = mutableListOf<Community>()
                    joinedCommunities.forEach { communityId ->
                        if (communityId != null) {
                            db.collection("communities")
                                .document(communityId)
                                .get()
                                .addOnSuccessListener { document ->
                                    if (document != null) {
                                        val community = document.toObject<Community>()
                                        if (community != null) {
                                            communities.add(community.copy(communityId = document.id))
                                            onJoinedCommunitiesUpdated(communities)
                                        } else {
                                            println("Community is null")
                                        }
                                    } else {
                                        println("No such document")
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    println("Error getting document: $exception")
                                }
                        }
                    }
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
// adaug un user intr-o comunitate
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
// verific daca un user este membru al unei comunitati
    fun isUserMemberOfCommunity(communityId: String, userId: String, onComplete: (Boolean) -> Unit) {
        db.collection("community_users")
            .whereEqualTo("communityId", communityId)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                onComplete(documents.size() > 0)
            }
            .addOnFailureListener { e ->
                println("Error checking membership: $e")
            }
    }
// sterg un user dintr-o comunitate
 fun leaveCommunity(communityId: String, userId: String){
    db.collection("community_users")
        .whereEqualTo("communityId", communityId)
        .whereEqualTo("userId", userId)
        .get()
        .addOnSuccessListener { querySnapshot ->
            if (querySnapshot.isEmpty) {
                println("No members found to delete")
                return@addOnSuccessListener
            }
            for (document in querySnapshot) {
                document.reference.delete()
                    .addOnSuccessListener {
                        println("DocumentSnapshot successfully deleted!")
                    }
                    .addOnFailureListener { e ->
                        println("Error deleting document: $e")
                    }
            }
        }
        .addOnFailureListener{ e ->
            println("Error searching for member: $e")
        }
}
// iau numele unei comunitati bazat pe id
    fun getCommunityName(communityId: String, onComplete: (String) -> Unit) {
        if (communityId.isNotBlank()) {
            db.collection("communities")
                .document(communityId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val community = document.toObject<Community>()
                        onComplete(community?.name ?: "")
                    } else {
                        println("No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    println("Error getting document: $exception")
                }
        } else {
            println("Invalid communityId")
        }
    }

    suspend fun getJoinedCommunities(userId: String): List<Community> {
        return try {
            val communityUsers = db.collection("community_users")
                .whereEqualTo("userId", userId)
                .get()
                .await()
                .map { it.getString("communityId") ?: "" }

            if (communityUsers.isEmpty()) {
                return emptyList()

            }

            val communities = db.collection("communities")
                .whereIn(com.google.firebase.firestore.FieldPath.documentId(), communityUsers)
                .get()
                .await()
                .map { it.toObject<Community>().copy(communityId = it.id) }

            communities
        } catch (e: Exception) {
            emptyList()
        }
    }
    fun isCommunityNameUnique(name: String, onComplete: (Boolean) -> Unit) {
        db.collection("communities")
            .whereEqualTo("name", name)
            .get()
            .addOnSuccessListener { documents ->
                onComplete(documents.isEmpty)
            }
            .addOnFailureListener { e ->
                println("Error checking community name: $e")
                onComplete(false)
            }
    }
    fun isUserCreatorOfCommunity(communityId: String, userId: String, onComplete: (Boolean) -> Unit) {
        db.collection("communities")
            .document(communityId)
            .get()
            .addOnSuccessListener { document ->
                val community = document.toObject<Community>()
                onComplete(community?.createdBy == userId)
            }
            .addOnFailureListener { e ->
                println("Error checking creator: $e")
                onComplete(false)
            }
    }
}