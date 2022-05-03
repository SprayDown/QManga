package org.spray.qmanga.ui.impl.preview

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.spray.qmanga.client.models.MangaData
import org.spray.qmanga.client.source.Source

class PreviewModelFactory(val source: Source, val data: MangaData) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(PreviewViewModel::class.java)) {
            PreviewViewModel(source, data) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }

}