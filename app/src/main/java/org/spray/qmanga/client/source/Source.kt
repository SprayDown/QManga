package org.spray.qmanga.client.source

import android.content.Context
import org.spray.qmanga.client.models.*

abstract class Source() {

    abstract val domain: String

    val sortTypes = arrayListOf<SortType>()
    val tags = arrayListOf<MangaTag>()

    open suspend fun loadPopular(): List<MangaData> {
        throw UnsupportedOperationException("Source ($domain) does not support 'Popular' mangas")
    }

    open suspend fun loadNewest(): List<MangaData> {
        throw UnsupportedOperationException("Source ($domain) does not support 'Newest' mangas")
    }

    open suspend fun loadSimilar(dir: String): List<MangaData> {
        throw UnsupportedOperationException("Source ($domain) does not support 'Similar' mangas")
    }

    abstract suspend fun search(query: String): List<MangaData>
    abstract suspend fun loadDetails(data: MangaData): MangaDetails
    abstract suspend fun loadPages(chapter: MangaChapter): List<MangaPage>
    abstract suspend fun loadChapters(branchId: Long): List<MangaChapter>
    abstract suspend fun loadList(
        page: Int,
        count: Int,
        queryData: QueryData,
        sortType: SortType?,
        listType: ListType
    ): List<MangaData>
}