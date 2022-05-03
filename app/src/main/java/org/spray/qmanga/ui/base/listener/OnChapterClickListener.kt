package org.spray.qmanga.ui.base.listener

import org.spray.qmanga.client.models.MangaChapter

interface OnChapterClickListener {

    fun onChapterClick(chapter: MangaChapter)
    fun onDownloadClick(chapter: MangaChapter)

}