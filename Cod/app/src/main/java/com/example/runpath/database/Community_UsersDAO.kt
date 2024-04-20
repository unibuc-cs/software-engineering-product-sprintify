package com.example.runpath.database

import FeedReaderDbHelper
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class Community_UsersDAO (context : Context, dbHelper: FeedReaderDbHelper) {
    private val db: SQLiteDatabase = dbHelper.writableDatabase

    fun insertCommunity_Users(
        communityId: Int,
        userId: Int,
        dateJoined: String
    ): Long {
        val values = ContentValues().apply {
            put(DataBase.CommunityUsersEntry.COLUMN_COMMUNITY_ID, communityId)
            put(DataBase.CommunityUsersEntry.COLUMN_USER_ID, userId)
            put(DataBase.CommunityUsersEntry.COLUMN_DATE_JOINED, dateJoined)
        }
        return db.insert(DataBase.CommunityUsersEntry.TABLE_NAME, null, values)
    }

    fun getCommunity_UsersByCommunityId(communityId: Int): Cursor {
        return db.rawQuery(
            "SELECT * FROM ${DataBase.CommunityUsersEntry.TABLE_NAME} WHERE ${DataBase.CommunityUsersEntry.COLUMN_COMMUNITY_ID} = ?",
            arrayOf(communityId.toString())
        )
    }

    fun updateCommunity_Users(
        communityId: Int,
        userId: Int,
        dateJoined: String
    ): Int {
        val values = ContentValues().apply {
            put(DataBase.CommunityUsersEntry.COLUMN_COMMUNITY_ID, communityId)
            put(DataBase.CommunityUsersEntry.COLUMN_USER_ID, userId)
            put(DataBase.CommunityUsersEntry.COLUMN_DATE_JOINED, dateJoined)
        }
        return db.update(
            DataBase.CommunityUsersEntry.TABLE_NAME,
            values,
            "${DataBase.CommunityUsersEntry.COLUMN_COMMUNITY_ID} = ? AND ${DataBase.CommunityUsersEntry.COLUMN_USER_ID} = ?",
            arrayOf(communityId.toString(), userId.toString())
        )
    }

    fun deleteCommunity_Users(communityId: Int, userId: Int): Int {
        return db.delete(
            DataBase.CommunityUsersEntry.TABLE_NAME,
            "${DataBase.CommunityUsersEntry.COLUMN_COMMUNITY_ID} = ? AND ${DataBase.CommunityUsersEntry.COLUMN_USER_ID} = ?",
            arrayOf(communityId.toString(), userId.toString())
        )
    }
}