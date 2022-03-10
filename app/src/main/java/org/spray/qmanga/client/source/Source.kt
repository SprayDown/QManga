package org.spray.qmanga.client.source

import android.content.Context
import org.spray.qmanga.client.models.MangaData
import org.spray.qmanga.client.models.MangaDetails

abstract class Source() {

    abstract val domain: String

    open suspend fun loadPopular(context: Context): List<MangaData> {
        throw UnsupportedOperationException("Source ($domain) does not support 'Popular' mangas")
    }

    open suspend fun loadNewest(context: Context): List<MangaData> {
        throw UnsupportedOperationException("Source ($domain) does not support 'Newest' mangas")
    }

    abstract suspend fun loadDetails(context: Context, data: MangaData): MangaDetails
}