package com.example.runpath.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlin.time.Duration

class LeaderboardDAO(context: Context, dbHelper: SQLiteOpenHelper) {

    private val db: SQLiteDatabase = dbHelper.writableDatabase

    fun insertLeaderboard(
        leaderboardId: Int,
        circuitId: Int,
        userId: Int,
        rank: Int,
        time: String
    ): Long {
        val values = ContentValues().apply {
            put(DataBase.LeaderboardEntry.COLUMN_LEADERBOARD_ID, leaderboardId)
            put(DataBase.LeaderboardEntry.COLUMN_CIRCUIT_ID, circuitId)
            put(DataBase.LeaderboardEntry.COLUMN_USER_ID, userId)
            put(DataBase.LeaderboardEntry.COLUMN_RANK, rank)
            put(DataBase.LeaderboardEntry.COLUMN_TIME, time)
        }
        return db.insert(DataBase.LeaderboardEntry.TABLE_NAME, null, values)
    }

    fun getLeaderboardById(leaderboardId: Int): Cursor {
        return db.rawQuery(
            "SELECT * FROM ${DataBase.LeaderboardEntry.TABLE_NAME} WHERE ${DataBase.LeaderboardEntry.COLUMN_LEADERBOARD_ID} = ?",
            arrayOf(leaderboardId.toString())
        )
    }

    fun updateLeaderboard(
        leaderboardId: Int,
        circuitId: Int,
        userId: Int,
        rank: Int,
        time: String
    ): Int {
        val values = ContentValues().apply {
            circuitId?.let {put(DataBase.LeaderboardEntry.COLUMN_CIRCUIT_ID, it)}
            userId?. let {put(DataBase.LeaderboardEntry.COLUMN_USER_ID, it)}
            rank?. let {put(DataBase.LeaderboardEntry.COLUMN_RANK, it)}
            time?. let {put(DataBase.LeaderboardEntry.COLUMN_TIME, it.toString())}
        }
        return db.update(
            DataBase.LeaderboardEntry.TABLE_NAME,
            values,
            "${DataBase.LeaderboardEntry.COLUMN_LEADERBOARD_ID} = ?",
            arrayOf(leaderboardId.toString())
        )
    }

    fun deleteLeaderboard(leaderboardId: Int): Int {
        return db.delete(
            DataBase.LeaderboardEntry.TABLE_NAME,
            "${DataBase.LeaderboardEntry.COLUMN_LEADERBOARD_ID} = ?",
            arrayOf(leaderboardId.toString())
        )
    }

}