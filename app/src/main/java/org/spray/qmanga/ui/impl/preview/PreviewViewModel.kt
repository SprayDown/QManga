package org.spray.qmanga.ui.impl.preview

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import org.spray.qmanga.client.local.LocalMangaManager
import org.spray.qmanga.client.models.MangaChapter
import org.spray.qmanga.client.models.MangaData
import org.spray.qmanga.client.models.MangaDetails
import org.spray.qmanga.client.source.Source
import org.spray.qmanga.sqlite.QueryResponse
import org.spray.qmanga.sqlite.query.ChapterQuery
import org.spray.qmanga.ui.base.BaseViewModel
import org.spray.qmanga.ui.impl.library.download.DownloadService

class PreviewViewModel(val source: Source, val data: MangaData) :
    BaseViewModel() {

    val mDetails = MutableLiveData<MangaDetails>()
    val mChapters = MutableLiveData<List<MangaChapter>>()

    val localList = ArrayList<MangaChapter>()
    val queueList = ArrayList<MangaChapter>()

    init {
        loadDetails()
    }

    private fun loadDetails() {
        job = launchLoadingJob(Dispatchers.Default) {
            try {
                mDetails.postValue(source.loadDetails(data))
            } catch (e: Exception) {
                loadLocalDetails(data.hashId)
            }
        }
    }

    fun loadChapters(details: MangaDetails) {
        job = launchJob(Dispatchers.Default) {
            val localManga = LocalMangaManager.loadManga(data.hashId)
            val chapters = ArrayList(source.loadChapters(details.branchId))

            val local = LocalMangaManager.parseChapters(localManga?.path, data.hashId)
            if (local != null) {
                localList.clear()
                localList.addAll(local)
            }

            queueList.addAll(DownloadService.queued.filterKeys { it == data.hashId }.values.filter {
                !queueList.contains(it)
            })

            if (chapters.isEmpty())
                local?.let { chapters.addAll(it) }

            val query = ChapterQuery(data.hashId)
            query.readMangaChapters(data.hashId, object : QueryResponse<List<MangaChapter>> {
                override fun onSuccess(data: List<MangaChapter>) {
                    data.forEach {
                        chapters.forEach { chapter ->
                            if (chapter.equalsChapter(it)) {
                                chapter.read = it.read
                            }
                        }
                    }
                }

                override fun onFailure(msg: String) {
                }

            })

            mChapters.postValue(chapters)
        }
    }

    fun onDownloadComplete(chapter: MangaChapter) {
        if (!localList.contains(chapter)) {
            localList.add(chapter)
        }
    }

    fun onDownloadQueued(chapter: MangaChapter?, queue: Boolean) {
        val state = queue && !queueList.contains(chapter)
        if (state)
            chapter?.let { queueList.add(it) }
        else if (!state)
            queueList.clear()
    }

    private fun loadLocalDetails(hashId: Int) {
        mDetails.postValue(LocalMangaManager.loadDetails(hashId))
    }
}