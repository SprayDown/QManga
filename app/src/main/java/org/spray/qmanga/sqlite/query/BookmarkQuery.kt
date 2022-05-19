package org.spray.qmanga.sqlite.query

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import org.spray.qmanga.client.models.MangaBookmark
import org.spray.qmanga.sqlite.*
import java.util.*

class BookmarkQuery : BaseQuery<MangaBookmark>(TABLE_MANGA_BOOKMARKS, MANGA_ID) {

    companion object {
        fun createTable(db: SQLiteDatabase?) {
            db?.execSQL(
                "create table $TABLE_MANGA_BOOKMARKS ("
                        + "$ID integer primary key autoincrement,"
                        + "$MANGA_NAME text,"
                        + "$MANGA_IMG text,"
                        + "$MANGA_URL text,"
                        + "$MANGA_RATING text,"
                        + "$MANGA_TYPE text,"
                        + "$MANGA_CHAPTERS_COUNT integer,"
                        + "$MANGA_ID integer,"
                        + "$WRITE_TIME integer" + ")"
            )
        }
    }

    override fun getContent(data: MangaBookmark): ContentValues {
        return ContentValues().apply {
            put(MANGA_NAME, data.name)
            put(MANGA_IMG, data.imageUrl)
            put(MANGA_URL, data.url)
            put(MANGA_RATING, data.rating)
            put(MANGA_TYPE, data.type)
            put(MANGA_ID, data.hashId)
            put(MANGA_CHAPTERS_COUNT, data.countChapters)
            put(WRITE_TIME, Date().time)
        }
    }

    override fun getData(cursor: Cursor): MangaBookmark {
        return MangaBookmark(
            name = cursor.getString(cursor.getColumnIndexOrThrow(MANGA_NAME)),
            imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(MANGA_IMG)),
            url = cursor.getString(cursor.getColumnIndexOrThrow(MANGA_URL)),
            rating = cursor.getString(cursor.getColumnIndexOrThrow(MANGA_RATING)),
            type = cursor.getString(cursor.getColumnIndexOrThrow(MANGA_TYPE)),
            countChapters = cursor.getInt(cursor.getColumnIndexOrThrow(MANGA_CHAPTERS_COUNT))
        )
    }
}