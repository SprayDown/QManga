package org.spray.qmanga.ui.impl.preview.chapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import org.spray.qmanga.R
import org.spray.qmanga.client.models.MangaChapter
import org.spray.qmanga.client.models.MangaData
import org.spray.qmanga.databinding.ItemChapterBinding
import org.spray.qmanga.ui.base.BaseAdapter
import org.spray.qmanga.ui.reader.ReaderActivity
import kotlin.jvm.internal.Intrinsics

class PreviewChaptersAdapter(
    dataSet: List<MangaChapter>,
    val mangaData: MangaData,
    fragmentActivity: FragmentActivity?
) :
    BaseAdapter<MangaChapter, PreviewChaptersAdapter.ChapterHolder>(
        dataSet, fragmentActivity
    ) {

    override fun createVH(parent: ViewGroup, viewType: Int): ChapterHolder {
        val inflater =
            LayoutInflater.from(parent.context).inflate(R.layout.item_chapter, parent, false)
        val holder = ChapterHolder(inflater)

        inflater.setOnClickListener {
            val activity = fragmentActivity as AppCompatActivity

            val intent = Intent(activity, ReaderActivity::class.java).apply {
                putExtra("data", mangaData)
                putExtra("chapter", holder.chapter)
                putParcelableArrayListExtra("chapters", ArrayList(getDataSet()))
            }
            activity.startActivity(intent)
        }
        return holder
    }

    override fun bind(holder: ChapterHolder, chapter: MangaChapter, position: Int) {
        holder.bind(chapter)
    }

    fun getChapter(tome: Int, number: String): MangaChapter {
        return getDataSet().first { it.tome == tome && it.number == number }
    }

    class ChapterHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val binding = ItemChapterBinding.bind(item)
        lateinit var chapter: MangaChapter

        fun bind(
            chapter: MangaChapter
        ) = with(binding) {
            this@ChapterHolder.chapter = chapter
            textViewTome.text = chapter.tome.toString()
            textViewNumber.text = "Глава " + chapter.number
            if (chapter.publisher != null) {
                textViewPublisher.text = chapter.publisher
                imageViewPerson.visibility = View.VISIBLE
            }
            textViewDate.text = chapter.date
        }
    }

    override fun setDataSet(data: List<MangaChapter>) {
        val diffResult = DiffUtil.calculateDiff(
            DiffCallback(
                getDataSet(),
                data
            ), true
        )
        (getDataSet() as ArrayList).clear()
        (getDataSet() as ArrayList).addAll(data)
        diffResult.dispatchUpdatesTo(this)
    }

    class DiffCallback(
        private val oldList: List<MangaChapter>,
        private val newList: List<MangaChapter>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return Intrinsics.areEqual(
                oldList[oldItemPosition].name,
                newList[newItemPosition].name
            ) &&
                    Intrinsics.areEqual(
                        oldList[oldItemPosition].tome,
                        newList[newItemPosition].tome
                    )
        }

        override fun getChangePayload(oldItem: Int, newItem: Int): Any? {
            if (oldList[oldItem] != newList[newItem]) {
                return newList[newItem]
            }
            return null
        }
    }
}