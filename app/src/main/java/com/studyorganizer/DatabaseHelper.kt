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
        const val DB_VERSION = 5 // Bumped version for timetable table

        const val TABLE         = "resources"
        const val COL_ID        = "id"
        const val COL_TITLE     = "title"
        const val COL_LINK      = "link"
        const val COL_CATEGORY  = "category"
        const val COL_TAGS      = "tags"
        const val COL_IMPORTANT = "is_important"
        const val COL_TYPE      = "type"
        const val COL_CREATED   = "created_at"

        const val TABLE_ATTENDANCE = "attendance"
        const val COL_ATT_ID       = "id"
        const val COL_ATT_SUBJECT  = "subject"
        const val COL_ATT_TOTAL    = "total_classes"
        const val COL_ATT_ATTENDED = "attended_classes"

        const val TABLE_TIMETABLE  = "timetable"
        const val COL_TIME_ID      = "id"
        const val COL_TIME_SUBJECT = "subject"
        const val COL_TIME_DAY     = "day"
        const val COL_TIME_START   = "start_time"
        const val COL_TIME_END     = "end_time"
        const val COL_TIME_TYPE    = "type"
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

        db.execSQL("""
            CREATE TABLE $TABLE_ATTENDANCE (
                $COL_ATT_ID       INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_ATT_SUBJECT  TEXT    NOT NULL UNIQUE,
                $COL_ATT_TOTAL    INTEGER DEFAULT 0,
                $COL_ATT_ATTENDED INTEGER DEFAULT 0
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_TIMETABLE (
                $COL_TIME_ID      INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_TIME_SUBJECT TEXT    NOT NULL,
                $COL_TIME_DAY     TEXT    NOT NULL,
                $COL_TIME_START   TEXT    NOT NULL,
                $COL_TIME_END     TEXT    NOT NULL,
                $COL_TIME_TYPE    TEXT    NOT NULL
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, old: Int, new: Int) {
        if (old < 4) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS $TABLE_ATTENDANCE (
                    $COL_ATT_ID       INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COL_ATT_SUBJECT  TEXT    NOT NULL UNIQUE,
                    $COL_ATT_TOTAL    INTEGER DEFAULT 0,
                    $COL_ATT_ATTENDED INTEGER DEFAULT 0
                )
            """.trimIndent())
        }
        if (old < 5) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS $TABLE_TIMETABLE (
                    $COL_TIME_ID      INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COL_TIME_SUBJECT TEXT    NOT NULL,
                    $COL_TIME_DAY     TEXT    NOT NULL,
                    $COL_TIME_START   TEXT    NOT NULL,
                    $COL_TIME_END     TEXT    NOT NULL,
                    $COL_TIME_TYPE    TEXT    NOT NULL
                )
            """.trimIndent())
        }
    }

    // ─── Resource CRUD ─────────────────────────────────────────────────────────

    fun addResource(r: Resource): Long =
        writableDatabase.insert(TABLE, null, r.toValues())

    fun getAllResources(): List<Resource> =
        queryResources(order = "$COL_IMPORTANT DESC, $COL_CREATED DESC")

    fun getByCategory(category: String): List<Resource> =
        queryResources(where = "$COL_CATEGORY = ?", args = arrayOf(category))

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

    // ─── Attendance CRUD ───────────────────────────────────────────────────────

    fun upsertAttendance(a: Attendance): Long {
        val cv = ContentValues().apply {
            put(COL_ATT_SUBJECT, a.subject)
            put(COL_ATT_TOTAL, a.totalClasses)
            put(COL_ATT_ATTENDED, a.attendedClasses)
        }
        return writableDatabase.insertWithOnConflict(
            TABLE_ATTENDANCE, null, cv, SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    fun getAttendanceBySubject(subject: String): Attendance? {
        val cursor = readableDatabase.query(
            TABLE_ATTENDANCE, null, "$COL_ATT_SUBJECT = ?", arrayOf(subject), null, null, null
        )
        return if (cursor.moveToFirst()) {
            val att = cursor.toAttendance()
            cursor.close()
            att
        } else {
            cursor.close()
            null
        }
    }

    fun getAllAttendance(): List<Attendance> {
        val list = mutableListOf<Attendance>()
        val cursor = readableDatabase.query(TABLE_ATTENDANCE, null, null, null, null, null, "$COL_ATT_SUBJECT ASC")
        cursor.use {
            while (it.moveToNext()) {
                list.add(it.toAttendance())
            }
        }
        return list
    }

    fun deleteAttendance(id: Int): Int =
        writableDatabase.delete(TABLE_ATTENDANCE, "$COL_ATT_ID = ?", arrayOf("$id"))

    // ─── Timetable CRUD ────────────────────────────────────────────────────────

    fun addTimetable(t: Timetable): Long {
        val cv = ContentValues().apply {
            put(COL_TIME_SUBJECT, t.subject)
            put(COL_TIME_DAY, t.day)
            put(COL_TIME_START, t.startTime)
            put(COL_TIME_END, t.endTime)
            put(COL_TIME_TYPE, t.type)
        }
        return writableDatabase.insert(TABLE_TIMETABLE, null, cv)
    }

    fun getTimetableByDay(day: String): List<Timetable> {
        val list = mutableListOf<Timetable>()
        val cursor = readableDatabase.query(
            TABLE_TIMETABLE, null, "$COL_TIME_DAY = ?", arrayOf(day), null, null, "$COL_TIME_START ASC"
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(it.toTimetable())
            }
        }
        return list
    }

    fun deleteTimetable(id: Int): Int =
        writableDatabase.delete(TABLE_TIMETABLE, "$COL_TIME_ID = ?", arrayOf("$id"))

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

    private fun queryResources(
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

    private fun Cursor.toAttendance() = Attendance(
        id              = getInt(getColumnIndexOrThrow(COL_ATT_ID)),
        subject         = getString(getColumnIndexOrThrow(COL_ATT_SUBJECT)),
        totalClasses    = getInt(getColumnIndexOrThrow(COL_ATT_TOTAL)),
        attendedClasses = getInt(getColumnIndexOrThrow(COL_ATT_ATTENDED))
    )

    private fun Cursor.toTimetable() = Timetable(
        id      = getInt(getColumnIndexOrThrow(COL_TIME_ID)),
        subject = getString(getColumnIndexOrThrow(COL_TIME_SUBJECT)),
        day     = getString(getColumnIndexOrThrow(COL_TIME_DAY)),
        startTime = getString(getColumnIndexOrThrow(COL_TIME_START)),
        endTime   = getString(getColumnIndexOrThrow(COL_TIME_END)),
        type      = getString(getColumnIndexOrThrow(COL_TIME_TYPE))
    )
}
