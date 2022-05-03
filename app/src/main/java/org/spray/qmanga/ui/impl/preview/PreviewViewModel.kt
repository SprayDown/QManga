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

            val localChapters = LocalMangaManager.parseChapters(localManga?.path, data.hashId)

            if (chapters.isEmpty())
                localChapters?.let { chapters.addAll(it) }

            if (localChapters?.isNotEmpty() == true)
                localChapters.all { local ->
                    chapters.all {
                        if (it.equalsChapter(local))
                            it.local = true
                        true
                    }
                }

            mChapters.postValue(chapters)
        }
    }

    private fun loadLocalDetails(hashId: Int) {
        local = true
        mDetails.postValue(LocalMangaManager.loadDetails(hashId))
    }
}