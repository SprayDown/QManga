package org.spray.qmanga.sqlite.query

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import android.util.Log
import org.spray.qmanga.sqlite.DBHelper
import org.spray.qmanga.sqlite.QueryResponse


abstract class BaseQuery<T>(protected val table: String, protected val mId: String) {

    protected val dbHelper = DBHelper.getInstance()

    abstract fun getContent(data: T): ContentValues
    abstract fun getData(cursor: Cursor): T

    fun create(data: T, response: QueryResponse<Long>?) {
        val sqlite = dbHelper.writableDatabase

        val contents = getContent(data)

        try {
            val id = sqlite.insertOrThrow(table, null, contents)
            if (id > 0) {
                response?.onSuccess(id)
                return
            }

            response?.onFailure("Failed to create data.")

        } catch (e: SQLiteConstraintException) {
            e.message?.let { Log.i("qmanga", it) }
            e.message?.let { response?.onFailure(it) }
        } finally {
            sqlite.close()
        }
    }

    fun createOrUpdate(data: T, hashId: Int, response: QueryResponse<Long>?) {
        if (exist(hashId.toString())) {
            update(data, hashId, null)
        } else
            create(data, response)
    }


    fun readAll(order: String?, response: QueryResponse<List<T>>?) {
        val sqlite = dbHelper.readableDatabase

        val list = arrayListOf<T>()
        var cursor: Cursor? = null

        try {
            cursor = sqlite.query(table, null, null, null, null, null, order)

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

    fun read(id: Int, response: QueryResponse<T>?) {
        val sqlite = dbHelper.readableDatabase

        val cursor: Cursor?
        try {
            cursor = sqlite.query(table, null, "$mId =? ", arrayOf(id.toString()), null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                response?.onSuccess(getData(cursor))
            } else
                response?.onFailure("There are no data in database")

        } catch (e: SQLiteException) {
            e.message?.let { response?.onFailure(it) }
        } finally {
//            sqlite.close()
        }
    }

    fun update(data: T, id: Int, response: QueryResponse<Boolean>?) {
        val sqlite = dbHelper.writableDatabase

        try {
            val count = sqlite.update(
                table, getContent(data), "$mId =? ",
                arrayOf(id.toString())
            )
            if (count > 0) {
                response?.onSuccess(true)
            } else
                response?.onFailure("No data is updated")

        } catch (e: SQLiteException) {
            e.message?.let { response?.onFailure(it) }
        } finally {
            sqlite.close()
        }
    }

    fun delete(id: Int, response: QueryResponse<Boolean>?) {
        val sqlite = dbHelper.writableDatabase

        try {
            val count = sqlite.delete(
                table, "$mId =? ",
                arrayOf(id.toString())
            )
            if (count > 0) {
                response?.onSuccess(true)
            } else
                response?.onFailure("Failed to delete data")

        } catch (e: SQLiteException) {
            e.message?.let { response?.onFailure(it) }
        } finally {
            sqlite.close()
        }
    }

    open fun exist(searchItem: String): Boolean {
        val selection = "$mId =?"
        val limit = "1"
        val cursor: Cursor =
            dbHelper.readableDatabase.query(
                table,
                arrayOf(mId),
                selection,
                arrayOf(searchItem),
                null,
                null,
                null,
                limit
            )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    open fun getId(hashId: Int): Int {
        val selection = "$mId =?"
        val limit = "1"
        val cursor: Cursor =
            dbHelper.readableDatabase.query(
                table,
                arrayOf(mId, ID),
                selection,
                arrayOf(hashId.toString()),
                null,
                null,
                null,
                limit
            )
        var id = -1
        if (cursor != null && cursor.moveToFirst())
            id = cursor.getInt(cursor.getColumnIndexOrThrow(ID))
        cursor.close()
        return id
    }

    companion object {
        const val ID = "_id"
    }
}