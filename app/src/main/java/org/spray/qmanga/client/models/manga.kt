package org.spray.qmanga.client.models

data class MangaData(val name: String,
                     val imageUrl: String,
                     val url: String,
                     val rating: Float,
                     val type:String,
                     val author: String?)

data class MangaDetails(val data: MangaData,
                        val description: String?,
                        val avg_rating: Float?,
                        val count_rating: Int?,
                        val issue_year: String?,
                        val total_views: Int?,
                        val total_voices: Int?,
                        val previewImgUrl: String,
                        val status: String,
                        val tag: Set<MangaTag>,
                        var chapters: List<MangaChapter>? = null)

data class MangaChapter(
    val id: Long,
    val name: String,
    val tome: Int,
    val number: Int,
    val date: String?,
    val publisher: String? = null)

data class MangaTag(val name: String, val id: Int)