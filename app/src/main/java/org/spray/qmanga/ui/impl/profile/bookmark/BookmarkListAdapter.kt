package org.spray.qmanga.ui.impl.profile.bookmark

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.ImageScaleType
import org.spray.qmanga.R
import org.spray.qmanga.client.models.MangaBookmark
import org.spray.qmanga.client.models.MangaData
import org.spray.qmanga.databinding.ItemBookmarkBinding
import org.spray.qmanga.ui.base.BaseAdapter
import org.spray.qmanga.ui.base.listener.OnItemClickListener
import org.spray.qmanga.ui.impl.preview.PreviewActivity
import kotlin.jvm.internal.Intrinsics

class BookmarkListAdapter(
    dataSet: List<MangaBookmark>, fragmentActivity: FragmentActivity?,
    var listener: OnItemClickListener<MangaData>? = null
) :
    BaseAdapter<MangaBookmark, BookmarkListAdapter.BookmarkHolder>(
        dataSet, fragmentActivity
    ) {

    private var options: DisplayImageOptions = DisplayImageOptions.Builder()
        .showImageOnLoading(R.drawable.empty)
        .showImageForEmptyUri(R.drawable.empty)
        .cacheInMemory(true)
        .cacheOnDisk(true)
        .considerExifParams(true)
        .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
        .build()

    override fun createVH(parent: ViewGroup, viewType: Int): BookmarkHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_bookmark,
            parent,
            false
        )
        val holder = BookmarkHolder(view)

        view.setOnClickListener {
            val activity = fragmentActivity as AppCompatActivity

            val intent = Intent(activity, PreviewActivity::class.java).apply {
                putExtra("manga_data", holder.data)
            }
            activity.startActivity(intent)
            listener?.onItemClick(holder.data)
        }

        view.setOnLongClickListener {
            listener?.onLongClick(holder.data)
            true
        }

        return holder
    }

    override fun bind(
        holder: BookmarkHolder,
        data: MangaBookmark,
        position: Int
    ) {
        holder.bind(data, fragmentActivity, options)
    }

    open class BookmarkHolder(val item: View) : RecyclerView.ViewHolder(item) {
        private val binding = ItemBookmarkBinding.bind(item)
        lateinit var data: MangaBookmark

        fun bind(
            manga: MangaBookmark,
            fragmentActivity: FragmentActivity?,
            options: DisplayImageOptions
        ) = with(binding) {
            data = manga
            textViewName.text = data.name
            val chaptersLeft = data.countChapters - data.readProgress
            textViewLastChapter.text = "Осталось глав $chaptersLeft"

            if (fragmentActivity != null) {
                ImageLoader.getInstance().displayImage(
                    data.imageUrl,
                    imageView,
                    options
                )
            }
        }
    }


    override fun setDataSet(data: List<MangaBookmark>) {
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
        private val oldList: List<MangaData>,
        private val newList: List<MangaData>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].hashId == newList[newItemPosition].hashId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return Intrinsics.areEqual(
                oldList[oldItemPosition].name,
                newList[newItemPosition].name
            ) &&
                    Intrinsics.areEqual(
                        oldList[oldItemPosition].url,
                        newList[newItemPosition].url
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