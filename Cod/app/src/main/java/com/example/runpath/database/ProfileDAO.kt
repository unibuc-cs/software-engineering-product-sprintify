package com.example.runpath.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.runpath.models.Profile
import com.google.firebase.firestore.FirebaseFirestore

class ProfileDAO(context: Context) {
    // creez o noua instanta a bazei de date
    private val db = FirebaseFirestore.getInstance()
    // creez un nou profil
    fun insertProfile(
        profile: Profile,
        userId: String,
        onComplete: (Profile) -> Unit
    ) {
        val documentReference = db.collection("profiles").document(userId)
        val newProfile = profile.copy(userId = userId)

        documentReference.set(newProfile)
            .addOnSuccessListener {
                onComplete(newProfile)
            }
            .addOnFailureListener { e ->
                println("Error adding document: $e")
            }

    }
    // obtin un profil dupa id
    fun getProfileById(userId: String,onComplete: (Profile) -> Unit){

        db.collection("profiles")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val profile = Profile(
                        userId = document.id,
                        preferredTerrain = document.getLong(DataBase.ProfileEntry.COLUMN_PREFERRED_TERRAIN)?.toInt() ?: 0,
                        preferredLightLevel = document.getLong(DataBase.ProfileEntry.COLUMN_PREFERRED_LIGHT_LEVEL)?.toInt() ?: 0,
                        isPetOwner = document.getBoolean(DataBase.ProfileEntry.COLUMN_PET_OWNER) ?: false
                    )
                    onComplete(profile)
                } else {
                    println("No such document")
                }
            }
    }
    // actualizez un profil
    fun updateProfile(
        userId: String,
        preferredTerrain: Int,
        preferredLightLevel: Int,
        isPetOwner: Boolean
    ) {
       val profile = Profile(
              userId = userId,
           preferredTerrain = preferredTerrain,
           preferredLightLevel = preferredLightLevel,
           isPetOwner = isPetOwner
       )
       db.collection("profiles").document(userId)
           .set(profile)
           .addOnSuccessListener {
               println("DocumentSnapshot successfully written!")
           }
           .addOnFailureListener { e ->
               println("Error adding document: $e")
           }


    }
    // sterg un profil
    fun deleteProfile(userId: String){
        db.collection("profiles").document(userId)
            .delete()
            .addOnSuccessListener {
                println("DocumentSnapshot successfully deleted!")
            }
            .addOnFailureListener { e ->
                println("Error deleting document: $e")
            }

    }
}