package org.spray.qmanga.ui.impl.mangalist

import android.content.Context
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import org.spray.qmanga.client.models.MangaData
import org.spray.qmanga.client.source.Source
import org.spray.qmanga.ui.impl.BaseViewModel

class MangaListViewModel(val context: Context, val source: Source) : BaseViewModel() {

    val newestList = MutableLiveData<List<MangaData>>()
    val popularList = MutableLiveData<List<MangaData>>()

    init {
        loadList()
    }

    fun loadList() {
        job = launchLoadingJob(Dispatchers.Default) {
            val newestResponse = source.loadNewest(context)
            val popularResponse = source.loadPopular(context)

            newestList.postValue(newestResponse)
            popularList.postValue(popularResponse)
        }
    }
}