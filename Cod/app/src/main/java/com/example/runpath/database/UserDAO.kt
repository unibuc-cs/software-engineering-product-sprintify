package com.example.runpath.database

import android.content.Context
import com.example.runpath.models.User
import com.example.runpath.others.*
import com.example.runpath.utils.PasswordUtilsNoDeps
import com.google.firebase.firestore.FirebaseFirestore
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class UserDAO(context: Context) {
    private val db = FirebaseFirestore.getInstance()

    fun insertUser(user: User, onComplete: (User) -> Unit) {
        val documentReference = db.collection("users").document()
        val userId = documentReference.id

        // 1) Generate "salt:hash" from the plaintext password
        val saltedHash = PasswordUtilsNoDeps.generateSaltedHash(user.password)

        // Store only that salted hash in place of the password
        val newUser = user.copy(
            userId = userId,
            password = saltedHash
        )

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


    fun getUserById(userId: String, onComplete: (User?) -> Unit) {

        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = User(
                        userId = document.id,
                        username = document.getString(DataBase.UserEntry.COLUMN_USERNAME) ?: "",
                        email = document.getString(DataBase.UserEntry.COLUMN_EMAIL) ?: "",
                        // This is now "salt:hash"
                        password = document.getString(DataBase.UserEntry.COLUMN_PASSWORD) ?: "",
                        dateCreated = document.getString(DataBase.UserEntry.COLUMN_DATE_CREATED) ?: ""
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

    /**
     *  Login flow:
     *    1) fetch user by username
     *    2) verify candidate password with the stored salted-hash
     */
    fun login(username: String, password: String, onComplete: (String) -> Unit) {
        getUserByUsername(username) { users ->
            println("Entered login with username $username")

            if (users.isEmpty()) {
                println("No users found with this username")
                onComplete(USER_NOT_FOUND)
            } else {
                for (user in users) {
                    // user.password is now the "salt:hash" string
                    val storedSaltedHash = user.password

                    // Compare the user-supplied password with the stored salted-hash
                    if (user.userId != null && PasswordUtilsNoDeps.verifyPassword(password, storedSaltedHash)) {
                        println("User found with matching username & password")
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

    fun getUserByUsername(username: String, onComplete: (List<User>) -> Unit) {
        val users = mutableListOf<User>()

        db.collection("users")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val user = User(
                        userId = document.id,
                        username = document.getString(DataBase.UserEntry.COLUMN_USERNAME) ?: "",
                        email = document.getString(DataBase.UserEntry.COLUMN_EMAIL) ?: "",
                        // This is "salt:hash"
                        password = document.getString(DataBase.UserEntry.COLUMN_PASSWORD) ?: "",
                        dateCreated = document.getString(DataBase.UserEntry.COLUMN_DATE_CREATED) ?: ""
                    )
                    users.add(user)
                }
                onComplete(users)
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
                onComplete(emptyList())
            }
    }

    fun updateUser(
        userId: String,
        username: String,
        password: String,
        email: String,
        dateCreated: String
    ) {
        // If updating the password, hash it first
        val saltedHash = PasswordUtilsNoDeps.generateSaltedHash(password)

        val updatedUser = User(
            userId = userId,
            username = username,
            password = saltedHash,
            email = email,
            dateCreated = dateCreated
        )

        db.collection("users")
            .document(userId)
            .set(updatedUser)
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

    fun setPassword(userId: String, newPassword: String){
        // Hash the new password before saving
        val saltedHash = PasswordUtilsNoDeps.generateSaltedHash(newPassword)

        db.collection("users")
            .document(userId)
            .update(DataBase.UserEntry.COLUMN_PASSWORD, saltedHash)
            .addOnSuccessListener {
                println("DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e ->
                println("Error updating document: $e")
            }
    }
}
