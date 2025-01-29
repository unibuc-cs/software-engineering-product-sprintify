package com.example.runpath.database

import android.database.sqlite.SQLiteDatabase
import android.content.Context
import com.example.runpath.models.User
import com.example.runpath.others.*
import com.google.firebase.firestore.FirebaseFirestore
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class UserDAO(context: Context) {
    // creez o noua instanta a bazei de date
    private val db = FirebaseFirestore.getInstance()
    // creez un nou user
    fun insertUser(user: User, onComplete: (User) -> Unit) {

        val documentReference = db.collection("users").document()
        //firebase genereaza un id unic pentru fiecare document
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
    fun formatDate(timestamp: Long): String {
        return try {
            val instant = Instant.ofEpochMilli(timestamp)
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                .withZone(ZoneId.systemDefault())
            formatter.format(instant)
        } catch (e: Exception) {
            println("Error: $e")
            "Invalid Date"
        }
    }
    fun getDateCreated(userId: String, onComplete: (String) -> Unit) {
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val timestamp = document.getString(DataBase.UserEntry.COLUMN_DATE_CREATED)?.toLongOrNull()
                    val dateCreated = timestamp?.let { formatDate(it) } ?: "Invalid Date"
                    println("DocumentSnapshot data: ${document.data}")
                    onComplete(dateCreated)
                } else {
                    onComplete("Invalid Date")
                }
            }
            .addOnFailureListener { exception ->
                println("Error getting document: $exception")
                onComplete("Invalid Date")
            }
    }
    // obtin un user dupa id
    fun getUserById(userId: String, onComplete: (User?)-> Unit) {
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = User(
                        userId = document.id,
                        username = document.getString(DataBase.UserEntry.COLUMN_USERNAME) ?: "",
                        email = document.getString(DataBase.UserEntry.COLUMN_EMAIL) ?: "",
                        password = document.getString(DataBase.UserEntry.COLUMN_PASSWORD) ?: "",
                        dateCreated = document.getString(DataBase.UserEntry.COLUMN_DATE_CREATED)
                            ?: ""
                    )
                    onComplete(user)
                } else {
                    println("No such document")
                    onComplete(null)
                }
            }
            .addOnFailureListener { exception ->
                println("Error getting document: $exception")
                onComplete(null)
            }
    }
    // login pentru user
    fun login(username: String, password: String, onComplete: (String) -> Unit) {
        getUserByUsername(username) { users ->
            println("Entered login with username $username and password $password")
            if (users.isEmpty()) {
                println("No users were found with this username")
                onComplete(USER_NOT_FOUND)
            } else {
                for (user in users) {
                    println("User found with username ${user.username} and password ${user.password}")
                    println("User username is being compared with $username and password is being compared with $password")
                    if (user.password == password && user.userId != null) {
                        println("User found with username $username and password $password")
                        onComplete(user.userId)
                        return@getUserByUsername
                    } else if (user.userId == null) {
                        onComplete(ERROR_USER_ID_NULL)
                        return@getUserByUsername
                    }
                }
                onComplete(USER_NOT_FOUND)
            }
        }
    }
    // obtin userii dupa username
    fun getUserByUsername(username: String, onComplete: (List<User>) -> Unit) {
        println("Entered getUserByUsername with username $username!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
        val users = mutableListOf<User>()

        db.collection("users")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    println("document accessed")
                    val user = User(
                        userId = document.id,
                        username = document.getString(DataBase.UserEntry.COLUMN_USERNAME).toString(),
                        email = document.getString(DataBase.UserEntry.COLUMN_EMAIL).toString(),
                        password = document.getString(DataBase.UserEntry.COLUMN_PASSWORD).toString(),
                        dateCreated = document.getString(DataBase.UserEntry.COLUMN_DATE_CREATED).toString()
                    )
                    users.add(user)
                    println("su")
                    println("${document.id} => ${document.data}")
                }
                onComplete(users) // apeleaza onComplete cu lista de useri
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
                onComplete(emptyList()) // apeleaza onComplete cu o lista goala
            }
    }
    // actualizez un user
    fun updateUser(
        userId: String,
        username: String,
        password: String,
        email: String,
        dateCreated: String
    ) {
        val user = User(
            userId = userId,
            username = username,
            password = password,
            email = email,
            dateCreated = dateCreated
        )
        db.collection("users")
            .document(userId)
            .set(user)
            .addOnSuccessListener {
                println("DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e ->
                println("Error adding document: $e")
            }
    }

    fun deleteUser(userId: String) {
        db.collection("users")
            .document(userId)
            .delete()
            .addOnSuccessListener {
                println("DocumentSnapshot successfully deleted!")
            }
            .addOnFailureListener { e ->
                println("Error deleting document: $e")
            }
    }
    // setez parola
    fun setPassword(userId: String, password: String){

        db.collection("users")
            .document(userId)
            .update(DataBase.UserEntry.COLUMN_PASSWORD, password)
            .addOnSuccessListener {
                println("DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e ->
                println("Error updating document: $e")
            }

    }
}