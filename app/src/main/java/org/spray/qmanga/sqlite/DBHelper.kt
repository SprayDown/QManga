package org.spray.qmanga.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.spray.qmanga.QManga
import org.spray.qmanga.sqlite.query.BaseQuery.Companion.ID

class DBHelper(
    context: Context
) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {


    override fun onCreate(db: SQLiteDatabase?) {
        // Recent data
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

        // Local
        db?.execSQL(
            "create table $TABLE_MANGA_LOCAL ("
                    + "$ID integer primary key autoincrement,"
                    + "$MANGA_NAME text,"
                    + "$MANGA_IMG text,"
                    + "$MANGA_URL text,"
                    + "$MANGA_RATING text,"
                    + "$MANGA_TYPE text,"
                    + "$MANGA_ID integer,"
                    + "$LOCAL_PATH text,"
                    + "$MANGA_ENG_NAME text,"
                    + "$MANGA_DESCRIPTION text,"
                    + "$MANGA_AVG_RATING real,"
                    + "$MANGA_COUNT_RATING integer,"
                    + "$MANGA_ISSUE_YEAR text,"
                    + "$MANGA_TOTAL_VOICES integer,"
                    + "$MANGA_TOTAL_VIEWS integer,"
                    + "$MANGA_PREVIEW_IMG text,"
                    + "$MANGA_STATUS text,"
                    + "$WRITE_TIME integer" + ")"
        )
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
        private var INSTANCE: DBHelper? = null

        fun getInstance() =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: DBHelper(QManga.appContext).also {
                    INSTANCE = it
                }
            }

        const val DB_NAME = "qmanga"
        const val DB_VERSION = 1
    }
}