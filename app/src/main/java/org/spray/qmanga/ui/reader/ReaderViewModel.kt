package org.spray.qmanga.ui.reader

import android.content.Context
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
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
            mPages.postValue(source.loadPages(chapter))
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