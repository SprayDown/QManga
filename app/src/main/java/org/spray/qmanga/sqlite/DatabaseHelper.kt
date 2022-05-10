package org.spray.qmanga.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.spray.qmanga.QManga
import org.spray.qmanga.sqlite.query.BookmarkQuery
import org.spray.qmanga.sqlite.query.ChapterQuery
import org.spray.qmanga.sqlite.query.LocalMangaQuery
import org.spray.qmanga.sqlite.query.RecentQuery

class DatabaseHelper(
    context: Context
) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        BookmarkQuery.createTable(db)
        RecentQuery.createTable(db)
        LocalMangaQuery.createTable(db)
        ChapterQuery.createTable(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL("drop table if exists $TABLE_MANGA_RECENT")
        db?.execSQL("drop table if exists $TABLE_MANGA_LOCAL")

        onCreate(db)
    }

    override fun onOpen(db: SQLiteDatabase?) {
        super.onOpen(db)

        db?.execSQL("PRAGMA foreign_keys=ON;")
    }

    companion object {
        private var INSTANCE: DatabaseHelper? = null

        fun getInstance() =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: DatabaseHelper(QManga.appContext).also {
                    INSTANCE = it
                }
            }

        const val DB_NAME = "qmangaDebug"
        const val DB_VERSION = 1
    }
}