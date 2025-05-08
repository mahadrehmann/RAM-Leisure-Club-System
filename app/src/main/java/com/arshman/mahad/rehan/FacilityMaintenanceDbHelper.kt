// FacilityMaintenanceDbHelper.kt
package com.arshman.mahad.rehan

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class FacilityMaintenanceDbHelper(context: Context)
    : SQLiteOpenHelper(context, "facility_maintenance_cache.db", null, 1) {

    companion object {
        private const val TBL = "facility_maintenance"
        private const val COL_ID       = "id"
        private const val COL_FACILITY = "facility"
        private const val COL_DUEDATE  = "dueDate"
        private const val COL_IS_SYNCED = "isSynced"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
      CREATE TABLE $TBL (
        $COL_ID        TEXT PRIMARY KEY,
        $COL_FACILITY  TEXT,
        $COL_DUEDATE   TEXT,
        $COL_IS_SYNCED INTEGER
      )
    """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldV: Int, newV: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TBL")
        onCreate(db)
    }

    fun insertOrUpdate(m: FacilityMaintenance) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_ID,        m.id)
            put(COL_FACILITY,  m.facility)
            put(COL_DUEDATE,   m.dueDate)
            put(COL_IS_SYNCED, m.isSynced)
        }
        db.replace(TBL, null, cv)
    }

    fun getUnsynced(): List<FacilityMaintenance> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TBL WHERE $COL_IS_SYNCED=0", null)
        val out = mutableListOf<FacilityMaintenance>()
        while (cursor.moveToNext()) {
            out += FacilityMaintenance(
                id       = cursor.getString(cursor.getColumnIndexOrThrow(COL_ID)),
                facility = cursor.getString(cursor.getColumnIndexOrThrow(COL_FACILITY)),
                dueDate  = cursor.getString(cursor.getColumnIndexOrThrow(COL_DUEDATE)),
                isSynced = cursor.getInt(cursor.getColumnIndexOrThrow(COL_IS_SYNCED))
            )
        }
        cursor.close()
        return out
    }

    fun markSynced(id: String) {
        val db = writableDatabase
        val cv = ContentValues().apply { put(COL_IS_SYNCED, 1) }
        db.update(TBL, cv, "$COL_ID=?", arrayOf(id))
    }
}
