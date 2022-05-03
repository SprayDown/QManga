package org.spray.qmanga.client.models

data class MangaDetails(
    val name: String,
    val type: String,
    var eng_name: String? = null,
    val description: String?,
    val avg_rating: Float?,
    val count_rating: Int?,
    val issue_year: String?,
    val total_views: Int?,
    val total_voices: Int?,
    val previewImgUrl: String,
    val status: String,
    val tag: Set<MangaTag>,
    var author: String? = null,
    val branchId: Long,
    var count_chapters: Int = 0,
    val path: String? = null
)