package com.example.runpath.database

import FeedReaderDbHelper
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ProfileDAO (context: Context, dbHelper: FeedReaderDbHelper) {
    private val db: SQLiteDatabase = dbHelper.writableDatabase

    fun insertProfile(
        userId: Int,
        preferredTerrain: Int,
        preferredLightLevel: Int,
        isPetOwner: Boolean
    ): Long {
        val values = ContentValues().apply {
            put(DataBase.ProfileEntry.COLUMN_USER_ID, userId)
            put(DataBase.ProfileEntry.COLUMN_PREFERRED_TERRAIN, preferredTerrain)
            put(DataBase.ProfileEntry.COLUMN_PREFERRED_LIGHT_LEVEL, preferredLightLevel)
            put(DataBase.ProfileEntry.COLUMN_PET_OWNER, isPetOwner)
        }
        return db.insert(DataBase.ProfileEntry.TABLE_NAME, null, values)
    }

    fun getProfileById(userId: Int): Cursor {
        return db.rawQuery(
            "SELECT * FROM ${DataBase.ProfileEntry.TABLE_NAME} WHERE ${DataBase.ProfileEntry.COLUMN_USER_ID} = ?",
            arrayOf(userId.toString())
        )
    }

    fun updateProfile(
        userId: Int,
        preferredTerrain: Int?,
        preferredLightLevel: Int?,
        isPetOwner: Boolean?
    ): Int {
        val values = ContentValues().apply {
            put(DataBase.ProfileEntry.COLUMN_USER_ID, userId)
            preferredTerrain?.let { put(DataBase.ProfileEntry.COLUMN_PREFERRED_TERRAIN, it) }
            preferredLightLevel?.let { put(DataBase.ProfileEntry.COLUMN_PREFERRED_LIGHT_LEVEL, it) }
            isPetOwner?.let { put(DataBase.ProfileEntry.COLUMN_PET_OWNER, it) }
        }
        return db.update(
            DataBase.ProfileEntry.TABLE_NAME,
            values,
            "${DataBase.ProfileEntry.COLUMN_USER_ID} = ?",
            arrayOf(userId.toString())
        )
    }

    fun deleteProfile(userId: Int): Int {
        return db.delete(
            DataBase.ProfileEntry.TABLE_NAME,
            "${DataBase.ProfileEntry.COLUMN_USER_ID} = ?",
            arrayOf(userId.toString())
        )
    }
}