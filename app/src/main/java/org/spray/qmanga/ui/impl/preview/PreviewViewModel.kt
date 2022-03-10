package org.spray.qmanga.ui.impl.preview

import android.content.Context
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import org.spray.qmanga.client.models.MangaData
import org.spray.qmanga.client.models.MangaDetails
import org.spray.qmanga.client.source.Source
import org.spray.qmanga.ui.impl.BaseViewModel

class PreviewViewModel(val context: Context, val source: Source, private val data: MangaData) : BaseViewModel() {

    val mDetails = MutableLiveData<MangaDetails>()

    init {
        loadDetails()
    }

    private fun loadDetails() {
        job = launchLoadingJob(Dispatchers.Default) {
            val detailsResponse = source.loadDetails(context, data)

            mDetails.postValue(detailsResponse)
        }
    }

}