package org.spray.qmanga.client.models.local

import org.spray.qmanga.client.models.MangaData

class LocalManga(
    name: String,
    imageUrl: String,
    url: String,
    rating: String,
    type: String,
    var path: String? = String(),
    var eng_name: String? = null,
    val description: String?,
    val avg_rating: Float?,
    val count_rating: Int?,
    val issue_year: String?,
    val total_views: Int?,
    val total_voices: Int?,
    val previewImgUrl: String = String(),
    val status: String,
) : MangaData(name, imageUrl, url, rating, type)
