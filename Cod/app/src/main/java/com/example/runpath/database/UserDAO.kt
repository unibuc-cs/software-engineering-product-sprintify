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

    fun login(username: String, password: String): Int {
        val cursor = getUserByUsername(username)
        if (cursor.moveToFirst()) {
            val storedPassword =
                cursor.getString(cursor.getColumnIndexOrThrow(DataBase.UserEntry.COLUMN_PASSWORD))
            if (storedPassword == password) {
                //pastram id-ul userului logat in SharedPreferences
                val userId =
                    cursor.getInt(cursor.getColumnIndexOrThrow(DataBase.UserEntry.COLUMN_USER_ID))
                cursor.close()
                return userId
            }
        }
        cursor.close()
        return -1 // -1 indicates login failed
    }


    fun getUserByUsername(username: String): Cursor {
        return db.rawQuery(
            "SELECT * FROM ${DataBase.UserEntry.TABLE_NAME} WHERE ${DataBase.UserEntry.COLUMN_USERNAME} = ?",
            arrayOf(username)
        )
    }

    fun updateUser(
        userId: Int,
        username: String?,
        email: String?,
        password: String?,
        dataCreated: String?
    ): Int {
        val values = ContentValues().apply {
            username?.let { put(DataBase.UserEntry.COLUMN_USERNAME, it) }
            email?.let { put(DataBase.UserEntry.COLUMN_EMAIL, it) }
            password?.let { put(DataBase.UserEntry.COLUMN_PASSWORD, it) }
            dataCreated?.let { put(DataBase.UserEntry.COLUMN_DATE_CREATED, it) }
        }
        return db.update(
            DataBase.UserEntry.TABLE_NAME,
            values,
            "${DataBase.UserEntry.COLUMN_USER_ID} = ?",
            arrayOf(userId.toString())
        )
    }

    fun deleteUser(userId: Int): Int {
        return db.delete(
            DataBase.UserEntry.TABLE_NAME,
            "${DataBase.UserEntry.COLUMN_USER_ID} = ?",
            arrayOf(userId.toString())
        )
    }

    fun setPassword(userId: Int, password: String): Int {
        val values = ContentValues().apply {
            put(DataBase.UserEntry.COLUMN_PASSWORD, password)
        }
        return db.update(
            DataBase.UserEntry.TABLE_NAME,
            values,
            "${DataBase.UserEntry.COLUMN_USER_ID} = ?",
            arrayOf(userId.toString())
        )
    }
}