package com.example.runpath.database

import FeedReaderDbHelper
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SavedCircuitsDAO(context: Context, dbHelper: FeedReaderDbHelper) {
    private val db: SQLiteDatabase = dbHelper.writableDatabase

    //create
    fun addSavedCircuit(userId: Int, circuitId: Int): Long {
        val values = ContentValues().apply {
            put(DataBase.SavedCircuitsEntry.COLUMN_USER_ID, userId)
            put(DataBase.SavedCircuitsEntry.COLUMN_CIRCUIT_ID, circuitId)
        }
        return db.insert(DataBase.SavedCircuitsEntry.TABLE_NAME, null, values)
    }

    //read
    fun getSavedCircuitsByUserId(userId: Int): Cursor {
        return db.rawQuery(
            "SELECT * FROM ${DataBase.SavedCircuitsEntry.TABLE_NAME} WHERE ${DataBase.SavedCircuitsEntry.COLUMN_USER_ID} = ?",
            arrayOf(userId.toString())
        )
    }

    //update
    fun updateSavedCircuit(savedCircuitId: Int, newCircuitId: Int): Int {
        val values = ContentValues().apply {
            put(DataBase.SavedCircuitsEntry.COLUMN_CIRCUIT_ID, newCircuitId)
        }
        return db.update(
            DataBase.SavedCircuitsEntry.TABLE_NAME,
            values,
            "${DataBase.SavedCircuitsEntry.COLUMN_SAVED_CIRCUIT_ID} = ?",
            arrayOf(savedCircuitId.toString())
        )
    }

    //delete
    fun deleteSavedCircuit(savedCircuitId: Int): Int {
        return db.delete(
            DataBase.SavedCircuitsEntry.TABLE_NAME,
            "${DataBase.SavedCircuitsEntry.COLUMN_SAVED_CIRCUIT_ID} = ?",
            arrayOf(savedCircuitId.toString())
        )
    }
}