package com.example.runpath.database

import FeedReaderDbHelper
import android.database.sqlite.SQLiteDatabase
import android.content.Context
import android.content.ContentValues
import android.database.sqlite.SQLiteOpenHelper
import android.database.Cursor
import androidx.compose.ui.platform.LocalContext
import com.example.runpath.models.Post
import com.example.runpath.models.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import java.time.LocalDateTime


class UserDAO() {

    private val db = FirebaseFirestore.getInstance()
    fun insertUser(user: User, onComplete: (User) -> Unit) {
        val documentReference = db.collection("users").document()
        val userId = documentReference.id
        val newUser = user.copy(userId = userId)

        documentReference.set(newUser)
            .addOnSuccessListener {
                onComplete(newUser)
            }
            .addOnFailureListener { e ->
                println("Error adding document: $e")
            }

    }

    fun getUserById(userId: Int){
        db.collection("users")
            .document(userId.toString())
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

  /*  fun login(username: String, password: String): Int {

        val cursor = db.query(
            DataBase.UserEntry.TABLE_NAME,
            arrayOf(DataBase.UserEntry.COLUMN_USER_ID),
            "${DataBase.UserEntry.COLUMN_USERNAME} = ? AND ${DataBase.UserEntry.COLUMN_PASSWORD} = ?",
            arrayOf(username, password),
            null,
            null,
            null
        )

        val userId = if (cursor.moveToFirst()) {
            cursor.getInt(cursor.getColumnIndex(DataBase.UserEntry.COLUMN_USER_ID))
        } else {
            -1
        }

        cursor.close()
        return userId
    }*/


    fun getUserByUsername(username: String, onComplete: (List<User>) -> Unit){
        db.collection("users")
            .whereEqualTo(DataBase.UserEntry.COLUMN_USERNAME, username)
            .get()
            .addOnSuccessListener { documents ->
                val users = documents.map { it.toObject<User>().copy(userId = it.id) }
                onComplete(users)
            }
            .addOnFailureListener { e ->
                println("Error getting documents: $e")
            }
    }

//    fun updateUser(
//        userId: Int,
//        username: String?,
//        email: String?,
//        password: String?,
//        dataCreated: String?
//    ): Int {
//        val values = ContentValues().apply {
//            username?.let { put(DataBase.UserEntry.COLUMN_USERNAME, it) }
//            email?.let { put(DataBase.UserEntry.COLUMN_EMAIL, it) }
//            password?.let { put(DataBase.UserEntry.COLUMN_PASSWORD, it) }
//            dataCreated?.let { put(DataBase.UserEntry.COLUMN_DATE_CREATED, it) }
//        }
//        return db.update(
//            DataBase.UserEntry.TABLE_NAME,
//            values,
//            "${DataBase.UserEntry.COLUMN_USER_ID} = ?",
//            arrayOf(userId.toString())
//        )
//    }
//
//    fun deleteUser(userId: Int): Int {
//        return db.delete(
//            DataBase.UserEntry.TABLE_NAME,
//            "${DataBase.UserEntry.COLUMN_USER_ID} = ?",
//            arrayOf(userId.toString())
//        )
//    }
//
//    fun setPassword(userId: Int, password: String): Int {
//        val values = ContentValues().apply {
//            put(DataBase.UserEntry.COLUMN_PASSWORD, password)
//        }
//        return db.update(
//            DataBase.UserEntry.TABLE_NAME,
//            values,
//            "${DataBase.UserEntry.COLUMN_USER_ID} = ?",
//            arrayOf(userId.toString())
//        )
//    }
}