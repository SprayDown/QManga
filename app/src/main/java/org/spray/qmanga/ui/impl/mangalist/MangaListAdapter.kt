package org.spray.qmanga.ui.impl.mangalist

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
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer
import org.spray.qmanga.R
import org.spray.qmanga.client.models.MangaData
import org.spray.qmanga.databinding.ItemMangalistBinding
import org.spray.qmanga.ui.base.BaseAdapter
import org.spray.qmanga.ui.impl.preview.PreviewActivity
import kotlin.jvm.internal.Intrinsics

class MangaListAdapter(dataSet: List<MangaData>, fragmentActivity: FragmentActivity?) :
    BaseAdapter<MangaData, MangaListAdapter.MangaHolder>(
        dataSet, fragmentActivity
    ) {

    private var options: DisplayImageOptions = DisplayImageOptions.Builder()
        .showImageOnLoading(R.drawable.empty)
        .showImageForEmptyUri(R.drawable.empty)
        .cacheInMemory(true)
        .cacheOnDisk(true)
        .considerExifParams(true)
        .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
        .displayer(RoundedBitmapDisplayer(14))
        .build()

    override fun createVH(parent: ViewGroup, viewType: Int): MangaHolder {
        return MangaHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_mangalist, parent, false)
        )
    }

    override fun bind(holder: MangaHolder, review: MangaData, position: Int) {
        holder.bind(review, fragmentActivity, options)
    }

    class MangaHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val binding = ItemMangalistBinding.bind(item)

        fun bind(
            manga: MangaData,
            fragmentActivity: FragmentActivity?,
            options: DisplayImageOptions
        ) = with(binding) {

            textViewMname.text = manga.name
            textViewRating.text = manga.rating

            if (fragmentActivity != null) {
                ImageLoader.getInstance().displayImage(
                    manga.imageUrl,
                    imageViewManga,
                    options
                )
            }

            linearLayout.setOnClickListener {
                val activity = fragmentActivity as AppCompatActivity

                val intent = Intent(activity, PreviewActivity::class.java).apply {
                    putExtra("manga_data", manga)
                }
                activity.startActivity(intent)
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