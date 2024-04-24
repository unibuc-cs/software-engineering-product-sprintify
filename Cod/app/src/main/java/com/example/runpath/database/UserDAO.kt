package com.example.runpath.database

import android.database.sqlite.SQLiteDatabase
import android.content.Context
import android.content.ContentValues
import android.database.sqlite.SQLiteOpenHelper
import android.database.Cursor


class UserDAO (context: Context, dbHelper: SQLiteOpenHelper) {

    private val db: SQLiteDatabase = dbHelper.writableDatabase
    fun insertUser(userId: Int, username: String, email: String, password: String, dataCreated: String) : Long {
        val values = ContentValues().apply {
            put(DataBase.UserEntry.COLUMN_USER_ID, userId)
            put(DataBase.UserEntry.COLUMN_USERNAME, username)
            put(DataBase.UserEntry.COLUMN_EMAIL, email)
            put(DataBase.UserEntry.COLUMN_PASSWORD, password)
            put(DataBase.UserEntry.COLUMN_DATE_CREATED, dataCreated)
        }
        return db.insert(DataBase.UserEntry.TABLE_NAME, null, values)
    }

    fun getUserById(userId: Int): Cursor {
        return db.rawQuery(
            "SELECT * FROM ${DataBase.UserEntry.TABLE_NAME} WHERE ${DataBase.UserEntry.COLUMN_USER_ID} = ?",
            arrayOf(userId.toString())
        )
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