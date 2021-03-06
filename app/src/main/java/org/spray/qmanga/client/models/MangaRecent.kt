package org.spray.qmanga.client.models

class MangaRecent(
    name: String,
    imageUrl: String,
    url: String,
    rating: String,
    type: String,
    var id: Long? = null,
    val position: Int,
    val chapterId: Long,
    val chapterTome: Int,
    val chapterNumber: String
) : MangaData(name, imageUrl, url, rating, type)
