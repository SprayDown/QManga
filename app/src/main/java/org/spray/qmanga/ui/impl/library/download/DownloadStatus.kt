package org.spray.qmanga.ui.impl.library.download

import org.spray.qmanga.client.models.MangaChapter
import org.spray.qmanga.client.models.MangaData

sealed class DownloadStatus() {

    data class Queued(val data: MangaData, val chapter: MangaChapter) : DownloadStatus()
    data class Error(val message: String) : DownloadStatus()
    data class Completed(val data: MangaData, val chapter: MangaChapter) : DownloadStatus()
    data class Finished(val data: MangaData, val chapter: MangaChapter?) : DownloadStatus()
    data class Progress(val progress: Int): DownloadStatus()

}