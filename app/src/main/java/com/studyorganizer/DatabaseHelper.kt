package com.studyorganizer

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        const val DB_NAME    = "StudyResources.db"
        const val DB_VERSION = 3 // Bumped version again to include the new field

        const val TABLE         = "resources"
        const val COL_ID        = "id"
        const val COL_TITLE     = "title"
        const val COL_LINK      = "link"
        const val COL_CATEGORY  = "category"
        const val COL_TAGS      = "tags"
        const val COL_IMPORTANT = "is_important"
        const val COL_TYPE      = "type"         // New column for LINK, DRIVE, LOCAL
        const val COL_CREATED   = "created_at"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABLE (
                $COL_ID        INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_TITLE     TEXT    NOT NULL,
                $COL_LINK      TEXT    NOT NULL,
                $COL_CATEGORY  TEXT    NOT NULL,
                $COL_TAGS      TEXT    DEFAULT '',
                $COL_IMPORTANT INTEGER DEFAULT 0,
                $COL_TYPE      TEXT    DEFAULT 'LINK',
                $COL_CREATED   INTEGER
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, old: Int, new: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE")
        onCreate(db)
    }

    // ─── CRUD ────────────────────────────────────────────────────────────────

    fun addResource(r: Resource): Long =
        writableDatabase.insert(TABLE, null, r.toValues())

    fun getAllResources(): List<Resource> =
        query(order = "$COL_IMPORTANT DESC, $COL_CREATED DESC")

    fun getByCategory(category: String): List<Resource> =
        query(where = "$COL_CATEGORY = ?", args = arrayOf(category))

    fun search(q: String): List<Resource> {
        val like = "%$q%"
        val cursor = readableDatabase.rawQuery(
            """SELECT * FROM $TABLE
               WHERE $COL_TITLE LIKE ? OR $COL_CATEGORY LIKE ? OR $COL_TAGS LIKE ?
               ORDER BY $COL_IMPORTANT DESC, $COL_CREATED DESC""",
            arrayOf(like, like, like)
        )
        return cursor.toList()
    }

    fun getCategories(): List<String> {
        val list = mutableListOf<String>()
        val cursor = readableDatabase.rawQuery(
            "SELECT DISTINCT $COL_CATEGORY FROM $TABLE ORDER BY $COL_CATEGORY", null
        )
        cursor.use { while (it.moveToNext()) list.add(it.getString(0)) }
        return list
    }

    fun updateResource(r: Resource): Int =
        writableDatabase.update(TABLE, r.toValues(), "$COL_ID = ?", arrayOf("${r.id}"))

    fun toggleImportant(id: Int, important: Boolean): Int {
        val cv = ContentValues().apply { put(COL_IMPORTANT, if (important) 1 else 0) }
        return writableDatabase.update(TABLE, cv, "$COL_ID = ?", arrayOf("$id"))
    }

    fun deleteResource(id: Int): Int =
        writableDatabase.delete(TABLE, "$COL_ID = ?", arrayOf("$id"))

    // ─── HELPERS ───────────────────────────────────────────────────────────────

    private fun Resource.toValues() = ContentValues().apply {
        put(COL_TITLE,     title)
        put(COL_LINK,      link)
        put(COL_CATEGORY,  category)
        put(COL_TAGS,      tags)
        put(COL_IMPORTANT, if (isImportant) 1 else 0)
        put(COL_TYPE,      type.name)
        put(COL_CREATED,   createdAt)
    }

    private fun query(
        where: String? = null,
        args: Array<String>? = null,
        order: String = "$COL_IMPORTANT DESC, $COL_CREATED DESC"
    ): List<Resource> =
        readableDatabase.query(TABLE, null, where, args, null, null, order).toList()

    private fun Cursor.toList(): List<Resource> {
        val list = mutableListOf<Resource>()
        use { while (it.moveToNext()) list.add(it.toResource()) }
        return list
    }

    private fun Cursor.toResource() = Resource(
        id          = getInt(getColumnIndexOrThrow(COL_ID)),
        title       = getString(getColumnIndexOrThrow(COL_TITLE)),
        link        = getString(getColumnIndexOrThrow(COL_LINK)),
        category    = getString(getColumnIndexOrThrow(COL_CATEGORY)),
        tags        = getString(getColumnIndexOrThrow(COL_TAGS)),
        isImportant = getInt(getColumnIndexOrThrow(COL_IMPORTANT)) == 1,
        type        = ResourceType.valueOf(getString(getColumnIndexOrThrow(COL_TYPE))),
        createdAt   = getLong(getColumnIndexOrThrow(COL_CREATED))
    )
}
