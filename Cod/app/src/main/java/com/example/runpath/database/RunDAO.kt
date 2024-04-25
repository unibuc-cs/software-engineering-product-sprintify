package com.example.runpath.database

import FeedReaderDbHelper
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.time.Instant
import kotlin.time.Duration

class RunDAO(context: Context, dbHelper: FeedReaderDbHelper) {

    private val db: SQLiteDatabase = dbHelper.writableDatabase

    fun insertRun(
        runId: Int,
        userId: Int,
        circuitId: Int,
        startTime: Instant,
        endTime: Instant,
        pauseTime: Duration,
        timeTracker: Duration,
        paceTracker: Double,
        distanceTracker: Double
        ): Long {
        val values = ContentValues().apply {
            put(DataBase.RunEntry.COLUMN_RUN_ID, runId)
            put(DataBase.RunEntry.COLUMN_USER_ID, userId)
            put(DataBase.RunEntry.COLUMN_CIRCUIT_ID, circuitId)
            put(DataBase.RunEntry.COLUMN_START_TIME, startTime.toString())
            put(DataBase.RunEntry.COLUMN_END_TIME, endTime.toString())
            put(DataBase.RunEntry.COLUMN_PAUSE_TIME, pauseTime.toString())
            put(DataBase.RunEntry.COLUMN_TIME_TRACKER, timeTracker.toString())
            put(DataBase.RunEntry.COLUMN_PACE_TRACKER, paceTracker)
            put(DataBase.RunEntry.COLUMN_DISTANCE_TRACKER, distanceTracker)
        }
        return db.insert(DataBase.RunEntry.TABLE_NAME, null, values)
    }

    fun getRunById(runId: Int): Cursor {
        return db.rawQuery(
            "SELECT * FROM ${DataBase.RunEntry.TABLE_NAME} WHERE ${DataBase.RunEntry.COLUMN_RUN_ID} = ?",
            arrayOf(runId.toString())
        )
    }

    fun updateRun(
        runId: Int,
        userId: Int,
        circuitId: Int,
        startTime: Instant,
        endTime: Instant,
        pauseTime: Duration,
        timeTracker: Duration,
        paceTracker: Double,
        distanceTracker: Double
    ): Int {
        val values = ContentValues().apply {
            userId?. let {put(DataBase.RunEntry.COLUMN_USER_ID, it)}
            circuitId?. let {put(DataBase.RunEntry.COLUMN_CIRCUIT_ID, it)}
            startTime?. let {put(DataBase.RunEntry.COLUMN_START_TIME, it.toString())}
            endTime?. let {put(DataBase.RunEntry.COLUMN_PAUSE_TIME, it.toString())}
            pauseTime?. let {put(DataBase.RunEntry.COLUMN_PAUSE_TIME, it.toString())}
            timeTracker?. let {put(DataBase.RunEntry.COLUMN_TIME_TRACKER, it.toString())}
            paceTracker?. let {put(DataBase.RunEntry.COLUMN_PACE_TRACKER, it)}
            distanceTracker?. let {put(DataBase.RunEntry.COLUMN_DISTANCE_TRACKER, it)}
        }
        return db.update(
            DataBase.RunEntry.TABLE_NAME,
            values,
            "${DataBase.RunEntry.COLUMN_RUN_ID} = ?",
            arrayOf(runId.toString())
        )
    }

    fun deleteRun(runId: Int): Int {
        return db.delete(
            DataBase.RunEntry.TABLE_NAME,
            "${DataBase.RunEntry.COLUMN_RUN_ID} = ?",
            arrayOf(runId.toString())
        )
    }
    
}