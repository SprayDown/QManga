package org.spray.qmanga.client.models

data class MangaPage(val mangas: List<MangaData>, val url: String, val hasNextPage: Boolean)
