package org.spray.qmanga.client.source

import org.spray.qmanga.client.models.MangaSource
import org.spray.qmanga.client.source.impl.ReMangaSource

class SourceManager {

    companion object {
        private val sources = mutableMapOf<MangaSource, Source>()

        fun getCurrentSource() =
            get(MangaSource.REMANGA)

        fun init() {
            register(MangaSource.REMANGA, ReMangaSource())
        }

        fun get(mangaSource: MangaSource): Source =
            sources[mangaSource] ?: throw IllegalArgumentException("Unknown Source")

        fun register(mangaSource: MangaSource, source: Source) {
            if (sources[mangaSource] == null)
                sources[mangaSource] = source;
            else
                throw IllegalArgumentException(mangaSource.name + " source already exists")
        }
    }
}