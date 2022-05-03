package org.spray.qmanga.sqlite.query

import android.content.ContentValues
import android.database.Cursor
import org.spray.qmanga.sqlite.*
import org.spray.qmanga.client.models.local.LocalManga
import java.util.*

class LocalMangaQuery : BaseQuery<LocalManga>(TABLE_MANGA_LOCAL, MANGA_ID) {

    override fun getData(cursor: Cursor): LocalManga {
        return LocalManga(
            name = cursor.getString(cursor.getColumnIndexOrThrow(MANGA_NAME)),
            imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(MANGA_IMG)),
            url = cursor.getString(cursor.getColumnIndexOrThrow(MANGA_URL)),
            rating = cursor.getString(cursor.getColumnIndexOrThrow(MANGA_RATING)),
            type = cursor.getString(cursor.getColumnIndexOrThrow(MANGA_TYPE)),
            path = cursor.getString(cursor.getColumnIndexOrThrow(LOCAL_PATH)),
            eng_name = cursor.getString(cursor.getColumnIndexOrThrow(MANGA_ENG_NAME)),
            description = cursor.getString(cursor.getColumnIndexOrThrow(MANGA_DESCRIPTION)),
            avg_rating = cursor.getFloat(cursor.getColumnIndexOrThrow(MANGA_AVG_RATING)),
            count_rating = cursor.getInt(cursor.getColumnIndexOrThrow(MANGA_COUNT_RATING)),
            issue_year = cursor.getString(cursor.getColumnIndexOrThrow(MANGA_ISSUE_YEAR)),
            total_views = cursor.getInt(cursor.getColumnIndexOrThrow(MANGA_TOTAL_VIEWS)),
            total_voices = cursor.getInt(cursor.getColumnIndexOrThrow(MANGA_TOTAL_VOICES)),
            previewImgUrl = cursor.getString(cursor.getColumnIndexOrThrow(MANGA_PREVIEW_IMG)),
            status = cursor.getString(cursor.getColumnIndexOrThrow(MANGA_STATUS)),
        )
    }

    override fun getContent(data: LocalManga): ContentValues {
        return ContentValues().apply {
            put(MANGA_NAME, data.name)
            put(MANGA_IMG, data.imageUrl)
            put(MANGA_URL, data.url)
            put(MANGA_RATING, data.rating)
            put(MANGA_TYPE, data.type)
            put(MANGA_ID, data.hashId)
            put(MANGA_ENG_NAME, data.eng_name)
            put(MANGA_DESCRIPTION, data.description)
            put(MANGA_AVG_RATING, data.avg_rating)
            put(MANGA_COUNT_RATING, data.count_rating)
            put(MANGA_ISSUE_YEAR, data.issue_year)
            put(MANGA_TOTAL_VOICES, data.total_voices)
            put(MANGA_TOTAL_VIEWS, data.total_views)
            put(MANGA_PREVIEW_IMG, data.previewImgUrl)
            put(MANGA_STATUS, data.status)
            put(LOCAL_PATH, data.path)
            put(WRITE_TIME, Date().time)
        }
    }
}
