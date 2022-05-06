package org.spray.qmanga.sqlite.query

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import org.spray.qmanga.client.models.MangaRecent
import org.spray.qmanga.sqlite.*
import java.util.*

class RecentQuery : BaseQuery<MangaRecent>(TABLE_MANGA_RECENT, MANGA_ID) {

    companion object {
        fun createTable(db: SQLiteDatabase?) {
            db?.execSQL(
                "create table $TABLE_MANGA_RECENT ("
                        + "$ID integer primary key autoincrement,"
                        + "$MANGA_NAME text,"
                        + "$MANGA_IMG text,"
                        + "$MANGA_URL text,"
                        + "$MANGA_RATING text,"
                        + "$MANGA_TYPE text,"
                        + "$MANGA_ID integer,"
                        + "$MANGA_CHAPTER_ID text,"
                        + "$MANGA_CHAPTER_TOME integer,"
                        + "$MANGA_CHAPTER_NUM text,"
                        + "$WRITE_TIME integer" + ")"
            )
        }
    }

    override fun getData(cursor: Cursor): MangaRecent {
        return MangaRecent(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(ID)),
            name = cursor.getString(cursor.getColumnIndexOrThrow(MANGA_NAME)),
            imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(MANGA_IMG)),
            url = cursor.getString(cursor.getColumnIndexOrThrow(MANGA_URL)),
            rating = cursor.getString(cursor.getColumnIndexOrThrow(MANGA_RATING)),
            type = cursor.getString(cursor.getColumnIndexOrThrow(MANGA_TYPE)),
            position = 0,
            chapterId = cursor.getLong(cursor.getColumnIndexOrThrow(MANGA_CHAPTER_ID)),
            chapterTome = cursor.getInt(cursor.getColumnIndexOrThrow(MANGA_CHAPTER_TOME)),
            chapterNumber = cursor.getString(cursor.getColumnIndexOrThrow(MANGA_CHAPTER_NUM))
        )
    }

    override fun getContent(data: MangaRecent): ContentValues {
        return ContentValues().apply {
            put(MANGA_NAME, data.name)
            put(MANGA_IMG, data.imageUrl)
            put(MANGA_URL, data.url)
            put(MANGA_RATING, data.rating)
            put(MANGA_TYPE, data.type)
            put(MANGA_ID, data.hashId)
            put(MANGA_CHAPTER_ID, data.chapterId)
            put(MANGA_CHAPTER_TOME, data.chapterTome)
            put(MANGA_CHAPTER_NUM, data.chapterNumber)
            put(WRITE_TIME, Date().time)
        }
    }

    fun getLast(response: QueryResponse<MangaRecent>) {
        val sqlite = dbHelper.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = sqlite.query(table, null, null, null, null, null, "$WRITE_TIME DESC", "1")

            if (cursor != null && cursor.moveToFirst()) {
                response.onSuccess(getData(cursor))
            } else
                response.onFailure("There are no data in database")

        } catch (e: SQLiteException) {
            e.message?.let { response.onFailure(it) }
        } finally {
            cursor?.close()
        }
    }
}