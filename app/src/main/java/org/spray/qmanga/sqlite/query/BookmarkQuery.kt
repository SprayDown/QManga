package org.spray.qmanga.sqlite.query

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import org.spray.qmanga.client.models.MangaData
import org.spray.qmanga.sqlite.*
import java.util.*

class BookmarkQuery : BaseQuery<MangaData>(TABLE_MANGA_BOOKMARKS, MANGA_ID) {

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
                        + "$MANGA_ID integer,"
                        + "$WRITE_TIME integer" + ")"
            )
        }
    }

    override fun getContent(data: MangaData): ContentValues {
        return ContentValues().apply {
            put(MANGA_NAME, data.name)
            put(MANGA_IMG, data.imageUrl)
            put(MANGA_URL, data.url)
            put(MANGA_RATING, data.rating)
            put(MANGA_TYPE, data.type)
            put(MANGA_ID, data.hashId)
            put(WRITE_TIME, Date().time)
        }
    }

    override fun getData(cursor: Cursor): MangaData {
        return MangaData(
            name = cursor.getString(cursor.getColumnIndexOrThrow(MANGA_NAME)),
            imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(MANGA_IMG)),
            url = cursor.getString(cursor.getColumnIndexOrThrow(MANGA_URL)),
            rating = cursor.getString(cursor.getColumnIndexOrThrow(MANGA_RATING)),
            type = cursor.getString(cursor.getColumnIndexOrThrow(MANGA_TYPE))
        )
    }
}