package org.spray.qmanga.ui.impl.mangalist

import android.content.Context
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import org.spray.qmanga.client.models.MangaChapter
import org.spray.qmanga.client.models.MangaData
import org.spray.qmanga.client.models.MangaDetails
import org.spray.qmanga.client.source.Source
import org.spray.qmanga.ui.base.BaseViewModel

class MangaListViewModel(val source: Source) : BaseViewModel() {

    val newestList = MutableLiveData<List<MangaData>>()
    val popularList = MutableLiveData<List<MangaData>>()

    fun loadList() {
        job = launchLoadingJob(Dispatchers.Default) {
            newestList.postValue(source.loadNewest())
            popularList.postValue(source.loadPopular())
        }
    }
}