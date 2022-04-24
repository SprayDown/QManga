package org.spray.qmanga.ui.reader

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.spray.qmanga.client.models.MangaChapter
import org.spray.qmanga.client.source.Source

class ReaderViewFactory(val context: Context, val source: Source, val chapter: MangaChapter) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(ReaderViewModel::class.java)) {
            ReaderViewModel(context, source, chapter) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}