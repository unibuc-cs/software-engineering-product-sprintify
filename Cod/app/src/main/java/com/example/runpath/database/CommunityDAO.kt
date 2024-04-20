package com.example.runpath.database

import FeedReaderDbHelper
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class CommunityDAO(context : Context, dbHelper: FeedReaderDbHelper){
    private val db: SQLiteDatabase = dbHelper.writableDatabase

    fun insertCommunity(
        communityId: Int,
        name: String,
        description: String
    ): Long {
        val values = ContentValues().apply {
            put(DataBase.CommunityEntry.COLUMN_COMMUNITY_ID, communityId)
            put(DataBase.CommunityEntry.COLUMN_NAME, name)
            put(DataBase.CommunityEntry.COLUMN_DESCRIPTION, description)

        }
        return db.insert(DataBase.CommunityEntry.TABLE_NAME, null, values)
    }

    fun getCommunityById(communityId : Int): Cursor {
        return db.rawQuery(
            "SELECT * FROM ${DataBase.CommunityEntry.TABLE_NAME} WHERE ${DataBase.CommunityEntry.COLUMN_COMMUNITY_ID} = ?",
            arrayOf(communityId.toString())
        )
    }

    fun updateCommunity(
        communityId: Int,
        name: String?,
        description: String?
    ): Int {
        val values = ContentValues().apply {
            put(DataBase.CommunityEntry.COLUMN_COMMUNITY_ID, communityId)
            name?.let { put(DataBase.CommunityEntry.COLUMN_NAME, it) }
            description?.let { put(DataBase.CommunityEntry.COLUMN_DESCRIPTION, it) }
        }
        return db.update(
            DataBase.CommunityEntry.TABLE_NAME,
            values,
            "${DataBase.CommunityEntry.COLUMN_COMMUNITY_ID} = ?",
            arrayOf(communityId.toString())
        )
    }

    fun deleteCommunity(communityId: Int): Int {
        return db.delete(
            DataBase.CommunityEntry.TABLE_NAME,
            "${DataBase.CommunityEntry.COLUMN_COMMUNITY_ID} = ?",
            arrayOf(communityId.toString())
        )
    }
}