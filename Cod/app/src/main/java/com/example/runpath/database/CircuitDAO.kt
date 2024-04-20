package com.example.runpath.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class CircuitDAO(context: Context, dbHelper: SQLiteOpenHelper) {
    private val db: SQLiteDatabase = dbHelper.writableDatabase


    fun insertCircuit(
        name: String,
        description: String,
        distance: Float,
        estimatedTime: Long,
        intensity: String,
        terrain: String,
        petFriendly: Boolean,
        lightLevel: Int,
        rating: Float,
        difficulty: String
    ): Long {
        val values = ContentValues().apply {
            put(DataBase.CircuitEntry.COLUMN_NAME, name)
            put(DataBase.CircuitEntry.COLUMN_DESCRIPTION, description)
            put(DataBase.CircuitEntry.COLUMN_DISTANCE, distance)
            put(DataBase.CircuitEntry.COLUMN_ESTIMATED_TIME, estimatedTime)
            put(DataBase.CircuitEntry.COLUMN_INTENSITY, intensity)
            put(DataBase.CircuitEntry.COLUMN_TERRAIN, terrain)
            put(DataBase.CircuitEntry.COLUMN_PET_FRIENDLY, if (petFriendly) 1 else 0)
            put(DataBase.CircuitEntry.COLUMN_LIGHT_LEVEL, lightLevel)
            put(DataBase.CircuitEntry.COLUMN_RATING, rating)
            put(DataBase.CircuitEntry.COLUMN_DIFFICULTY, difficulty)
        }
        return db.insert(DataBase.CircuitEntry.TABLE_NAME, null, values)
    }


    fun getCircuitById(circuitId: Int): Cursor {
        return db.rawQuery(
            "SELECT * FROM ${DataBase.CircuitEntry.TABLE_NAME} WHERE ${DataBase.CircuitEntry.COLUMN_CIRCUIT_ID} = ?",
            arrayOf(circuitId.toString())
        )
    }


    fun updateCircuit(
        circuitId: Int,
        name: String?,
        description: String?,
        distance: Float?,
        estimatedTime: Long?,
        intensity: String?,
        terrain: String?,
        petFriendly: Boolean?,
        lightLevel: Int?,
        rating: Float?,
        difficulty: String?
    ): Int {
        val values = ContentValues().apply {
            name?.let { put(DataBase.CircuitEntry.COLUMN_NAME, it) }
            description?.let { put(DataBase.CircuitEntry.COLUMN_DESCRIPTION, it) }
            distance?.let { put(DataBase.CircuitEntry.COLUMN_DISTANCE, it) }
            estimatedTime?.let { put(DataBase.CircuitEntry.COLUMN_ESTIMATED_TIME, it) }
            intensity?.let { put(DataBase.CircuitEntry.COLUMN_INTENSITY, it) }
            terrain?.let { put(DataBase.CircuitEntry.COLUMN_TERRAIN, it) }
            petFriendly?.let { put(DataBase.CircuitEntry.COLUMN_PET_FRIENDLY, if (it) 1 else 0) }
            lightLevel?.let { put(DataBase.CircuitEntry.COLUMN_LIGHT_LEVEL, it) }
            rating?.let { put(DataBase.CircuitEntry.COLUMN_RATING, it) }
            difficulty?.let { put(DataBase.CircuitEntry.COLUMN_DIFFICULTY, it) }
        }
        return db.update(
            DataBase.CircuitEntry.TABLE_NAME,
            values,
            "${DataBase.CircuitEntry.COLUMN_CIRCUIT_ID} = ?",
            arrayOf(circuitId.toString())
        )
    }


    fun deleteCircuit(circuitId: Int): Int {
        return db.delete(
            DataBase.CircuitEntry.TABLE_NAME,
            "${DataBase.CircuitEntry.COLUMN_CIRCUIT_ID} = ?",
            arrayOf(circuitId.toString())
        )
    }
}
