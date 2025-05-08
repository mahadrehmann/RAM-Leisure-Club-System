// AdminDbHelper.kt
package com.arshman.mahad.rehan

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AdminDbHelper(context: Context)
    : SQLiteOpenHelper(context, "admin_cache.db", null, 1) {

    companion object {
        private const val TBL            = "admin"
        private const val COL_ID         = "id"
        private const val COL_USERNAME   = "username"
        private const val COL_EMAIL      = "email"
        private const val COL_PASSWORD   = "password"
        private const val COL_PHONE      = "phone"
        private const val COL_NAME       = "name"
        private const val COL_DP         = "dp"
        private const val COL_IS_SYNCED  = "isSynced"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
      CREATE TABLE $TBL (
        $COL_ID        TEXT PRIMARY KEY,
        $COL_USERNAME  TEXT,
        $COL_EMAIL     TEXT,
        $COL_PASSWORD  TEXT,
        $COL_PHONE     TEXT,
        $COL_NAME      TEXT,
        $COL_DP        TEXT,
        $COL_IS_SYNCED INTEGER
      )
    """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldV: Int, newV: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TBL")
        onCreate(db)
    }

    fun insertOrUpdate(a: Admin) {
        writableDatabase.replace(TBL, null, ContentValues().apply {
            put(COL_ID,        a.id)
            put(COL_USERNAME,  a.username)
            put(COL_EMAIL,     a.email)
            put(COL_PASSWORD,  a.password)
            put(COL_PHONE,     a.phone)
            put(COL_NAME,      a.name)
            put(COL_DP,        a.dp)
            put(COL_IS_SYNCED, a.isSynced)
        })
    }

    fun markSynced(localId: String) {
        writableDatabase.update(
            TBL,
            ContentValues().apply { put(COL_IS_SYNCED, 1) },
            "$COL_ID=?",
            arrayOf(localId)
        )
    }

    fun getUnsynced(): List<Admin> {
        val out = mutableListOf<Admin>()
        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM $TBL WHERE $COL_IS_SYNCED=0", null
        )
        while (cursor.moveToNext()) {
            out += Admin(
                id       = cursor.getString(cursor.getColumnIndexOrThrow(COL_ID)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME)),
                email    = cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD)),
                phone    = cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE)),
                name     = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                dp       = cursor.getString(cursor.getColumnIndexOrThrow(COL_DP)),
                isSynced = cursor.getInt(cursor.getColumnIndexOrThrow(COL_IS_SYNCED))
            )
        }
        cursor.close()
        return out
    }
}
