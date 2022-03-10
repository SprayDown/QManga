package org.spray.qmanga.ui.impl.mangalist

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.spray.qmanga.client.source.Source

class MangaListViewFactory(val context: Context, val source: Source) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MangaListViewModel::class.java)) {
            MangaListViewModel(context, source) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}