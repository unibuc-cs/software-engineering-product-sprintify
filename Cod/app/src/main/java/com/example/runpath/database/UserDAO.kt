package com.example.runpath.database

import FeedReaderDbHelper
import android.database.sqlite.SQLiteDatabase
import android.content.Context
import android.content.ContentValues
import android.database.sqlite.SQLiteOpenHelper
import android.database.Cursor
import androidx.compose.ui.platform.LocalContext
import java.time.LocalDateTime



class UserDAO (context: Context, dbHelper: FeedReaderDbHelper) {

    private val db: SQLiteDatabase = dbHelper.writableDatabase
    fun insertUser(username: String, email: String, password: String) : Long {
        val values = ContentValues().apply {
            put(DataBase.UserEntry.COLUMN_USERNAME, username)
            put(DataBase.UserEntry.COLUMN_EMAIL, email)
            put(DataBase.UserEntry.COLUMN_PASSWORD, password)
            val dateCreated = LocalDateTime.now().toString()
            put(DataBase.UserEntry.COLUMN_DATE_CREATED, dateCreated)
        }
        return db.insert(DataBase.UserEntry.TABLE_NAME, null, values)
    }

    fun getUserById(userId: Int): Cursor {
        return db.rawQuery(
            "SELECT * FROM ${DataBase.UserEntry.TABLE_NAME} WHERE ${DataBase.UserEntry.COLUMN_USER_ID} = ?",
            arrayOf(userId.toString())
        )
    }

    fun login(username: String, password: String): Int {
        val cursor = getUserByUsername(username)
        if (cursor.moveToFirst()) {
            val storedPassword = cursor.getString(cursor.getColumnIndexOrThrow(DataBase.UserEntry.COLUMN_PASSWORD))
            if (storedPassword == password) {
                //pastram id-ul userului logat in SharedPreferences
                val userId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBase.UserEntry.COLUMN_USER_ID))
                cursor.close()
                return userId
            }        }
        cursor.close()
        return -1 // -1 indicates login failed
    }



    fun getUserByUsername(username: String): Cursor {
        return db.rawQuery(
            "SELECT * FROM ${DataBase.UserEntry.TABLE_NAME} WHERE ${DataBase.UserEntry.COLUMN_USERNAME} = ?",
            arrayOf(username)
        )
    }

    fun updateUser(userId: Int, username: String?, email: String?, password: String?, dataCreated: String?): Int {
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
}