package org.spray.qmanga.ui.impl.mangalist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.spray.qmanga.R
import org.spray.qmanga.client.models.MangaData
import org.spray.qmanga.databinding.ItemMangacardBinding
import org.spray.qmanga.ui.impl.BaseAdapter
import org.spray.qmanga.ui.impl.preview.PreviewFragment


class MangaListAdapter(dataSet: List<MangaData>, fragmentActivity: FragmentActivity?) : BaseAdapter<MangaData, MangaListAdapter.MangaHolder>(
    dataSet, fragmentActivity
) {

    override fun createVH(parent: ViewGroup, viewType: Int) : MangaHolder {
        return MangaHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_mangacard, parent, false))
    }

    override fun bind(holder: MangaHolder, review: MangaData, position: Int) {
        holder.bind(review, fragmentActivity)
    }

    class MangaHolder(item: View) : RecyclerView.ViewHolder(item), View.OnLongClickListener {
        private val binding = ItemMangacardBinding.bind(item)

        fun bind(manga: MangaData, fragmentActivity: FragmentActivity?) = with(binding) {
            mangaName.text = manga.name

            if (fragmentActivity != null) {
                Glide.with(fragmentActivity).asBitmap().load(manga.imageUrl).into(mangaImage)
            }

            itemView.setOnClickListener(View.OnClickListener {
                val activity = fragmentActivity as AppCompatActivity

                activity.supportFragmentManager.beginTransaction().replace(
                    R.id.fragmentContainerView, PreviewFragment(manga)
                ).addToBackStack(null).commit()
            })
        }

        override fun onLongClick(view: View?): Boolean {
            return false
        }
    }
}