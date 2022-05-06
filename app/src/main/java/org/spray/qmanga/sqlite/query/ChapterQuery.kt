package org.spray.qmanga.sqlite.query

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import org.spray.qmanga.client.models.MangaChapter
import org.spray.qmanga.sqlite.*

class ChapterQuery(val hashId: Int) :
    BaseQuery<MangaChapter>(TABLE_MANGA_CHAPTERS, MANGA_CHAPTER_ID) {

    companion object {
        fun createTable(db: SQLiteDatabase?) {
            db?.execSQL(
                "create table $TABLE_MANGA_CHAPTERS ("
                        + "$ID integer primary key autoincrement,"
                        + "$MANGA_ID integer,"
                        + "$MANGA_CHAPTER_ID integer,"
                        + "$MANGA_CHAPTER_NAME text,"
                        + "$MANGA_CHAPTER_TOME integer,"
                        + "$MANGA_CHAPTER_NUM text,"
                        + "$MANGA_CHAPTER_READ integer" + ")"
            )
        }
    }

    override fun getContent(data: MangaChapter): ContentValues {
        return ContentValues().apply {
            put(MANGA_ID, hashId) // chapter owner MangaData hashId
            put(MANGA_CHAPTER_ID, data.id)
            put(MANGA_CHAPTER_NAME, data.name)
            put(MANGA_CHAPTER_TOME, data.tome)
            put(MANGA_CHAPTER_NUM, data.number)
            put(MANGA_CHAPTER_READ, if (data.read) 1 else 0)
        }
    }

    override fun getData(cursor: Cursor): MangaChapter {
        return MangaChapter(
            cursor.getLong(cursor.getColumnIndexOrThrow(MANGA_CHAPTER_ID)),
            cursor.getString(cursor.getColumnIndexOrThrow(MANGA_CHAPTER_NAME)),
            cursor.getInt(cursor.getColumnIndexOrThrow(MANGA_CHAPTER_TOME)),
            cursor.getString(cursor.getColumnIndexOrThrow(MANGA_CHAPTER_NUM)),
            read = cursor.getInt(cursor.getColumnIndexOrThrow(MANGA_CHAPTER_READ)) == 1
        )
    }

    fun readMangaChapters(id: Int, response: QueryResponse<List<MangaChapter>>?) {
        val sqlite = dbHelper.readableDatabase

        val list = arrayListOf<MangaChapter>()
        var cursor: Cursor? = null

        try {
            cursor =
                sqlite.query(table, null, "$MANGA_ID =? ", arrayOf(id.toString()), null, null, null)

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    list.add(getData(cursor))
                } while (cursor.moveToNext())

                response?.onSuccess(list)
            } else
                response?.onFailure("There are no data in database")

        } catch (e: SQLiteException) {
            e.message?.let { response?.onFailure(it) }
        } finally {
            cursor?.close()
        }
    }
}