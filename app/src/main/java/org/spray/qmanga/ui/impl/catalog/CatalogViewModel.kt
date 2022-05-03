package org.spray.qmanga.ui.impl.catalog

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import org.spray.qmanga.client.models.ListType
import org.spray.qmanga.client.models.MangaData
import org.spray.qmanga.client.models.QueryData
import org.spray.qmanga.client.models.SortType
import org.spray.qmanga.client.source.Source
import org.spray.qmanga.ui.base.BaseViewModel

class CatalogViewModel(val source: Source) : BaseViewModel() {

    val mangas = MutableLiveData<List<MangaData>>()
    var queryData: QueryData = QueryData()
    var sortType: SortType? = SortType.POPULAR
    var listType = ListType.DESCENDING
    var page = 1
    var count = 30

    fun update(
    ) {
        job = launchLoadingJob(Dispatchers.Default) {
            mangas.postValue(source.loadList(page, count, queryData, sortType, listType))
        }
    }
}