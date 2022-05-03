package org.spray.qmanga.ui.reader.chapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import org.spray.qmanga.R
import org.spray.qmanga.client.models.MangaChapter
import org.spray.qmanga.databinding.ItemReaderChapterBinding
import org.spray.qmanga.ui.base.BaseAdapter
import org.spray.qmanga.ui.base.listener.OnItemClickListener

class ReaderChapterAdapter(
    dataSet: List<MangaChapter>,
    fragmentActivity: FragmentActivity?,
    var currentChapter: MangaChapter,
    private val listener: OnItemClickListener<MangaChapter>
) :
    BaseAdapter<MangaChapter, ReaderChapterAdapter.ChapterHolder>(
        dataSet, fragmentActivity
    ) {

    override fun createVH(parent: ViewGroup, viewType: Int): ChapterHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_reader_chapter, parent, false)
        val holder = ChapterHolder(view)

        view.setOnClickListener {
            val oldChapter = currentChapter
            currentChapter = holder.chapter
            listener.onItemClick(holder.chapter)
            notifyItemChanged(getDataSet().indexOf(getDataSet().first { it.tome == oldChapter.tome && it.number == oldChapter.number }))
            notifyItemChanged(holder.absoluteAdapterPosition)
        }
        return holder
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun bind(holder: ChapterHolder, data: MangaChapter, position: Int) {
        if (data.equalsChapter(currentChapter)) {
            holder.binding.chapterLayout.background =
                fragmentActivity?.getDrawable(R.drawable.selected_chapter)
        } else
            holder.binding.chapterLayout.background =
                fragmentActivity?.getDrawable(android.R.color.transparent)
        holder.bind(data)
    }

    class ChapterHolder(item: View) : RecyclerView.ViewHolder(item) {
        val binding = ItemReaderChapterBinding.bind(item)
        lateinit var chapter: MangaChapter

        fun bind(
            chapter: MangaChapter
        ) = with(binding) {
            textViewTome.text = "Том " + chapter.tome
            textViewNumber.text = "Глава " + chapter.number

            this@ChapterHolder.chapter = chapter
        }
    }
}
