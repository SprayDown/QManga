package org.spray.qmanga.ui.impl.preview.chapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import org.spray.qmanga.R
import org.spray.qmanga.client.models.MangaChapter
import org.spray.qmanga.databinding.ItemChapterBinding
import org.spray.qmanga.ui.impl.BaseAdapter

class ChaptersAdapter(dataSet: List<MangaChapter>, fragmentActivity: FragmentActivity?) :
    BaseAdapter<MangaChapter, ChaptersAdapter.ChapterHolder>(
        dataSet, fragmentActivity
    ) {

    override fun createVH(parent: ViewGroup, viewType: Int): ChapterHolder {
        return ChapterHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_chapter, parent, false)
        )
    }

    override fun bind(holder: ChapterHolder, data: MangaChapter, position: Int) {
        holder.bind(data)
    }

    class ChapterHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val binding = ItemChapterBinding.bind(item)

        fun bind(chapter: MangaChapter) = with(binding) {
            textViewTome.text = chapter.tome.toString()
            textViewNumber.text = "Глава " + chapter.number
            textViewPublisher.text = chapter.publisher
            textViewDate.text = chapter.date
        }
    }
}