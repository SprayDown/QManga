package org.spray.qmanga.ui.impl.search

import android.content.Context
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import org.spray.qmanga.client.models.MangaData
import org.spray.qmanga.client.source.Source
import org.spray.qmanga.ui.base.BaseViewModel

class SearchViewModel(val source: Source) : BaseViewModel() {

    val searchList = MutableLiveData<List<MangaData>>()

    fun search(query: String) {
        job = launchLoadingJob(Dispatchers.Default) {
            searchList.postValue(source.search(query))
        }
    }
}