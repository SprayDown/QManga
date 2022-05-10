package org.spray.qmanga.client.source

import org.spray.qmanga.client.models.MangaSource
import org.spray.qmanga.client.source.impl.ReMangaSource

class SourceManager {

    companion object {
        private val sources = mutableMapOf<MangaSource, Source>()
        private val currentSource = MangaSource.REMANGA

        fun getCurrentSource() =
            get(currentSource)

        fun getCurrentKey() = sources.entries.find { it.value == getCurrentSource() }!!.key

        fun init() {
            register(MangaSource.REMANGA, ReMangaSource())
        }

        fun get(mangaSource: MangaSource): Source =
            sources[mangaSource] ?: throw IllegalArgumentException("Unknown Source")

        private fun register(mangaSource: MangaSource, source: Source) {
            if (sources[mangaSource] == null)
                sources[mangaSource] = source;
            else
                throw IllegalArgumentException(mangaSource.name + " source already exists")
        }
    }
}