package org.spray.qmanga.ui.reader

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import org.spray.qmanga.client.local.LocalMangaManager
import org.spray.qmanga.client.models.MangaChapter
import org.spray.qmanga.client.models.MangaData
import org.spray.qmanga.client.models.MangaPage
import org.spray.qmanga.client.source.Source
import org.spray.qmanga.ui.base.BaseViewModel

class ReaderViewModel(
    val source: Source,
    var chapter: MangaChapter
) : BaseViewModel() {

    val mPages = MutableLiveData<List<MangaPage>>()
    val mChapters = MutableLiveData<List<MangaChapter>>()

    fun loadPages() {
        job = launchLoadingJob(Dispatchers.Default) {
            var pages = source.loadPages(chapter)
            if (chapter.localPath != null) {
                val local = LocalMangaManager.parsePages(chapter.localPath!!)

                if (pages.isEmpty())
                    pages = local
            }

            mPages.postValue(pages)
        }
    }

    fun loadChapters(data: MangaData) {
        job = launchLoadingJob(Dispatchers.Default) {
            val chapters = source.loadChapters(
                source.loadDetails(data).branchId
            )

            chapters.filter { !it.locked }
            mChapters.postValue(chapters)
        }
    }


}