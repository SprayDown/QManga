package org.spray.qmanga.sqlite

import org.spray.qmanga.client.models.MangaData

interface IRecentQuery {
    fun createRecent(data: MangaData, response: QueryResponse<Boolean>?)
    fun updateRecent(data: MangaData, response: QueryResponse<Boolean>)
    fun deleteRecent(id: Long, response: QueryResponse<Boolean>)
    fun readRecent(response: QueryResponse<List<MangaData>>)
}