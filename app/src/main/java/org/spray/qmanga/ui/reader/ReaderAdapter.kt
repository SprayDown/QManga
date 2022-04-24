package org.spray.qmanga.ui.reader

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.ImageScaleType
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener
import org.spray.qmanga.R
import org.spray.qmanga.client.models.MangaPage
import org.spray.qmanga.databinding.ItemPageBinding
import org.spray.qmanga.ui.base.BaseAdapter
import kotlin.jvm.internal.Intrinsics


class ReaderAdapter(dataSet: List<MangaPage>, fragmentActivity: FragmentActivity?) :
    BaseAdapter<MangaPage, ReaderAdapter.ImageHolder>(
        dataSet, fragmentActivity
    ) {

    private var options: DisplayImageOptions = DisplayImageOptions.Builder()
        .showImageOnLoading(R.drawable.empty)
        .showImageForEmptyUri(R.drawable.empty)
        .cacheInMemory(true)
        .considerExifParams(true)
        .bitmapConfig(Bitmap.Config.RGB_565)
        .imageScaleType(ImageScaleType.NONE)
        .build()

    override fun createVH(parent: ViewGroup, viewType: Int): ImageHolder {
        return ImageHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_page, parent, false)
        )
    }

    override fun bind(holder: ImageHolder, data: MangaPage, position: Int) {
        holder.bind(data, fragmentActivity, options)
    }

    class ImageHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val binding = ItemPageBinding.bind(item)

        fun bind(
            page: MangaPage,
            fragmentActivity: FragmentActivity?,
            options: DisplayImageOptions
        ) {

            binding.imageViewPage.maxWidth = page.width
            if (fragmentActivity != null)
                ImageLoader.getInstance().displayImage(
                    page.link,
                    binding.imageViewPage,
                    options,
                    object : SimpleImageLoadingListener() {
                        override fun onLoadingStarted(imageUri: String?, view: View?) {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        override fun onLoadingComplete(
                            imageUri: String?,
                            view: View?,
                            loadedImage: Bitmap?
                        ) {
                            binding.progressBar.visibility = View.GONE
                        }
                    })
        }
    }

    override fun setDataSet(data: List<MangaPage>) {
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
        private val oldList: List<MangaPage>,
        private val newList: List<MangaPage>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return Intrinsics.areEqual(
                oldList[oldItemPosition].page,
                newList[newItemPosition].page
            ) &&
                    Intrinsics.areEqual(
                        oldList[oldItemPosition].link,
                        newList[newItemPosition].link
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