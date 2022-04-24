package org.spray.qmanga.client.source

import android.content.Context
import org.spray.qmanga.client.models.*

abstract class Source() {

    abstract val domain: String

    val sortTypes = arrayListOf<SortType>()
    val tags = arrayListOf<MangaTag>()

    open suspend fun loadPopular(context: Context): List<MangaData> {
        throw UnsupportedOperationException("Source ($domain) does not support 'Popular' mangas")
    }

    open suspend fun loadNewest(context: Context): List<MangaData> {
        throw UnsupportedOperationException("Source ($domain) does not support 'Newest' mangas")
    }

    abstract suspend fun search(context: Context, query: String): List<MangaData>
    abstract suspend fun loadDetails(context: Context, data: MangaData): MangaDetails
    abstract suspend fun loadPages(context: Context, chapter: MangaChapter): List<MangaPage>
    abstract suspend fun loadChapters(context: Context, branchId: Long): List<MangaChapter>
    abstract suspend fun loadList(
        context: Context, page: Int,
        count: Int,
        queryData: QueryData,
        sortType: SortType?,
        listType: ListType
    ): List<MangaData>
}