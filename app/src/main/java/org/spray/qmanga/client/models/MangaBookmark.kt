package org.spray.qmanga.client.models

class MangaBookmark(
    name: String,
    imageUrl: String,
    url: String,
    rating: String = "0",
    type: String = String(),
    val readProgress: Int = 0,
    val readProgressTotal: Int = 0,
    val countChapters: Int = 0
) : MangaData(name, imageUrl, url, rating, type) {
}