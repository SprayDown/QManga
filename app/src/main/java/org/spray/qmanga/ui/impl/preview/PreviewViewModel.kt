package org.spray.qmanga.ui.impl.preview

import android.content.Context
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import org.spray.qmanga.client.models.MangaChapter
import org.spray.qmanga.client.models.MangaData
import org.spray.qmanga.client.models.MangaDetails
import org.spray.qmanga.client.source.Source
import org.spray.qmanga.sqlite.models.MangaRecent
import org.spray.qmanga.ui.base.BaseViewModel

class PreviewViewModel(val context: Context, val source: Source, private val data: MangaData) :
    BaseViewModel() {

    val mDetails = MutableLiveData<MangaDetails>()
    val mChapters = MutableLiveData<List<MangaChapter>>()

    init {
        loadDetails()
    }

    private fun loadDetails() {
        job = launchLoadingJob(Dispatchers.Default) {
            mDetails.postValue(source.loadDetails(context, data))
        }
    }

    fun loadChapters(details: MangaDetails) {
        job = launchJob(Dispatchers.Default) {
            mChapters.postValue(source.loadChapters(context, details.branchId))
        }
    }

}