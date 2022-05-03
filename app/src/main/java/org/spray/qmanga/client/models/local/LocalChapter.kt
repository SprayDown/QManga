package org.spray.qmanga.client.models.local

import org.spray.qmanga.client.models.MangaChapter

class LocalChapter(
    tome: Int,
    number: String,
    date: String?,
    var path: String
) : MangaChapter(0, String(), tome, number, date)