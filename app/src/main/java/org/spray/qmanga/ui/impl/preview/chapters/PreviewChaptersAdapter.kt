package org.spray.qmanga.ui.impl.preview.chapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import org.spray.qmanga.R
import org.spray.qmanga.client.models.MangaChapter
import org.spray.qmanga.databinding.ItemChapterBinding
import org.spray.qmanga.sqlite.query.ChapterQuery
import org.spray.qmanga.ui.base.BaseAdapter
import org.spray.qmanga.ui.base.listener.OnChapterClickListener
import org.spray.qmanga.ui.impl.preview.PreviewViewModel
import kotlin.jvm.internal.Intrinsics


class PreviewChaptersAdapter(
    fragmentActivity: FragmentActivity?,
    val viewModel: PreviewViewModel,
    val listener: OnChapterClickListener
) :
    BaseAdapter<MangaChapter, PreviewChaptersAdapter.ChapterHolder>(
        ArrayList(), fragmentActivity
    ) {

    val query = ChapterQuery(viewModel.data.hashId)

    override fun createVH(parent: ViewGroup, viewType: Int): ChapterHolder {
        val inflater =
            LayoutInflater.from(parent.context).inflate(R.layout.item_chapter, parent, false)
        val holder = ChapterHolder(inflater)

        inflater.setOnClickListener {
            listener.onChapterClick(holder.chapter)
        }

        return holder
    }

    override fun bind(holder: ChapterHolder, data: MangaChapter, position: Int) {
        holder.bind(data, listener)

        fragmentActivity ?: return

        with(holder.binding) {
            imageViewLocal.visibility =
                if (viewModel.localList.contains(data)) View.VISIBLE else View.GONE
            imageViewRefresh.visibility =
                if (viewModel.localList.contains(data)) View.VISIBLE else View.GONE

            if (viewModel.queueList.firstOrNull { it.equalsChapter(data) } != null) {
                imageViewDownload.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
            } else {
                imageViewDownload.visibility =
                    if (!(data.locked || viewModel.localList.contains(data))) View.VISIBLE else View.GONE
                progressBar.visibility = View.GONE
            }

            imageViewViewed.setOnClickListener {
                data.read = !data.read

                val fadeIn = AlphaAnimation(0f, 1f)
                fadeIn.interpolator = DecelerateInterpolator()
                fadeIn.duration = 500

                imageViewViewed.animation = fadeIn
                imageViewViewed.setImageResource(
                    if (data.read) R.drawable.ic_baseline_visibility_24
                    else R.drawable.ic_baseline_visibility_off_24
                )
                notifyItemChanged(position)

                query.createOrUpdate(data, data.id.toInt(), null)
            }
        }
    }

    class ChapterHolder(item: View) : RecyclerView.ViewHolder(item) {
        val binding = ItemChapterBinding.bind(item)
        lateinit var chapter: MangaChapter

        fun bind(
            chapter: MangaChapter,
            listener: OnChapterClickListener
        ) = with(binding) {
            this@ChapterHolder.chapter = chapter

            textViewNumber.alpha = if (chapter.read) 0.5f else 1f
            textViewNumber.text = "Том ${chapter.tome} Глава ${chapter.number}"
            if (chapter.date != null)
                textViewDate.text = chapter.date

            if (chapter.publisher != null) {
                textViewPublisher.text = chapter.publisher
                imageViewPerson.visibility = View.VISIBLE
            }

            imageViewLocked.visibility = if (chapter.locked) View.VISIBLE else View.GONE

            imageViewViewed.setImageResource(
                if (chapter.read) R.drawable.ic_baseline_visibility_24
                else R.drawable.ic_baseline_visibility_off_24
            )
            imageViewDownload.setOnClickListener { listener.onDownloadClick(chapter) }
            imageViewRefresh.setOnClickListener { listener.onDownloadClick(chapter) }
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