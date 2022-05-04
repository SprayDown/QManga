package org.spray.qmanga.ui.impl.preview

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import org.spray.qmanga.client.local.LocalMangaManager
import org.spray.qmanga.client.models.MangaChapter
import org.spray.qmanga.client.models.MangaData
import org.spray.qmanga.client.models.MangaDetails
import org.spray.qmanga.client.source.Source
import org.spray.qmanga.ui.base.BaseViewModel

class PreviewViewModel(val source: Source, private val data: MangaData) :
    BaseViewModel() {

    val mDetails = MutableLiveData<MangaDetails>()
    val mChapters = MutableLiveData<List<MangaChapter>>()

    val localList = ArrayList<MangaChapter>()
    val queueList = ArrayList<MangaChapter>()

    private var local = false

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

            if (chapters.isEmpty())
                local?.let { chapters.addAll(it) }

            mChapters.postValue(chapters)
        }
    }

    fun onDownloadComplete(chapter: MangaChapter) {
        if (!localList.contains(chapter)) {
            localList.add(chapter)
        }
    }

    fun onDownloadQueued(chapter: MangaChapter, queue: Boolean) {
        val state = queue && !queueList.contains(chapter)
        if (state)
            queueList.add(chapter)
        else if (!state)
            queueList.remove(chapter)
    }

    private fun loadLocalDetails(hashId: Int) {
        local = true
        mDetails.postValue(LocalMangaManager.loadDetails(hashId))
    }
}