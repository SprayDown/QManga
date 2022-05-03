package org.spray.qmanga.ui.impl.catalog

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.spray.qmanga.client.source.Source
import org.spray.qmanga.ui.impl.search.SearchViewModel

class CatalogViewFactory(val source: Source) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(CatalogViewModel::class.java)) {
            CatalogViewModel(source) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}