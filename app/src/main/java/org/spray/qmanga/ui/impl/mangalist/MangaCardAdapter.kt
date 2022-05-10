package org.spray.qmanga.ui.impl.mangalist

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.ImageScaleType
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer
import org.spray.qmanga.R
import org.spray.qmanga.client.models.MangaData
import org.spray.qmanga.databinding.ItemMangacardBinding
import org.spray.qmanga.databinding.ItemMangacardGridBinding
import org.spray.qmanga.ui.base.BaseAdapter
import org.spray.qmanga.ui.base.listener.OnItemClickListener
import org.spray.qmanga.ui.impl.preview.PreviewActivity
import kotlin.jvm.internal.Intrinsics


open class MangaCardAdapter(
    dataSet: List<MangaData>,
    fragmentActivity: FragmentActivity?,
    private val gridMode: Boolean = false,
    var listener: OnItemClickListener<MangaData>? = null
) :
    BaseAdapter<MangaData, MangaCardAdapter.MangaHolder>(
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

    override fun createVH(parent: ViewGroup, viewType: Int): MangaHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            if (gridMode) R.layout.item_mangacard_grid else R.layout.item_mangacard,
            parent,
            false
        )
        val holder = MangaHolder(view, gridMode)

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

    override fun bind(holder: MangaHolder, review: MangaData, position: Int) {
        holder.bind(review, fragmentActivity, options)
    }

    open class MangaHolder(val item: View, gridMode: Boolean) : RecyclerView.ViewHolder(item) {
        private val binding =
            if (gridMode) ItemMangacardGridBinding.bind(item) else ItemMangacardBinding.bind(item)
        lateinit var data: MangaData

        fun bind(
            manga: MangaData,
            fragmentActivity: FragmentActivity?,
            options: DisplayImageOptions
        ) = with(binding) {
            data = manga
            item.findViewById<TextView>(R.id.mangaName).text = manga.name
            val imageView = item.findViewById(R.id.mangaImage) as ImageView

            if (fragmentActivity != null) {
                ImageLoader.getInstance().displayImage(
                    manga.imageUrl,
                    imageView,
                    options
                )
            }
        }
    }

    override fun setDataSet(data: List<MangaData>) {
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