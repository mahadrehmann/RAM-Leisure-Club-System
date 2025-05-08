// BookingDbHelper.kt
package com.arshman.mahad.rehan

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BookingDbHelper(c: Context)
    : SQLiteOpenHelper(c, "booking_cache.db", null, 1) {

    companion object {
        private const val TBL = "bookings"
        private const val COL_ID        = "id"
        private const val COL_TITLE     = "title"
        private const val COL_DESC      = "description"
        private const val COL_TIME      = "time"
        private const val COL_DATE      = "date"
        private const val COL_USER_ID   = "userId"
        private const val COL_IS_SYNCED = "isSynced"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
      CREATE TABLE $TBL (
        $COL_ID        TEXT PRIMARY KEY,
        $COL_TITLE     TEXT,
        $COL_DESC      TEXT,
        $COL_TIME      TEXT,
        $COL_DATE      TEXT,
        $COL_USER_ID   TEXT,
        $COL_IS_SYNCED INTEGER
      )
    """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldV: Int, newV: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TBL")
        onCreate(db)
    }

    fun insertOrUpdate(b: Booking) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_ID,        b.id)
            put(COL_TITLE,     b.title)
            put(COL_DESC,      b.description)
            put(COL_TIME,      b.time)
            put(COL_DATE,      b.date)
            put(COL_USER_ID,   b.userId)
            put(COL_IS_SYNCED, b.isSynced)
        }
        db.replace(TBL, null, cv)
    }

    fun getUnsynced(): List<Booking> {
        val db = readableDatabase
        val cur = db.rawQuery("SELECT * FROM $TBL WHERE $COL_IS_SYNCED=0", null)
        val out = mutableListOf<Booking>()
        while (cur.moveToNext()) {
            out += Booking(
                id        = cur.getString(cur.getColumnIndexOrThrow(COL_ID)),
                title     = cur.getString(cur.getColumnIndexOrThrow(COL_TITLE)),
                description = cur.getString(cur.getColumnIndexOrThrow(COL_DESC)),
                time      = cur.getString(cur.getColumnIndexOrThrow(COL_TIME)),
                date      = cur.getString(cur.getColumnIndexOrThrow(COL_DATE)),
                userId    = cur.getString(cur.getColumnIndexOrThrow(COL_USER_ID)),
                isSynced  = cur.getInt(cur.getColumnIndexOrThrow(COL_IS_SYNCED))
            )
        }
        cur.close()
        return out
    }

    fun markSynced(id: String) {
        val db = writableDatabase
        val cv = ContentValues().apply { put(COL_IS_SYNCED, 1) }
        db.update(TBL, cv, "$COL_ID=?", arrayOf(id))
    }
}
