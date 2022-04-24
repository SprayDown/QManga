package org.spray.qmanga.ui.impl.catalog.filters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import org.spray.qmanga.R
import org.spray.qmanga.client.models.MangaTag
import org.spray.qmanga.ui.widgets.TagCheckBox


class TagsAdapter<T>(
    context: Context,
    private val resource: Int,
    objects: Array<MangaTag>,
    private val activeObjects: Array<MangaTag>
) :
    ArrayAdapter<MangaTag>(context, resource, objects) {

    private var activeTags = arrayListOf<MangaTag>()

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val tag: MangaTag? = getItem(position)

        val cv =
            LayoutInflater.from(context).inflate(resource, parent, false)
        val checkBox = cv.findViewById<TagCheckBox>(R.id.checkbox)

        checkBox.isChecked = activeObjects.contains(tag)

        if (tag != null) {
            if (checkBox.isChecked && !activeTags.contains(tag)) {
                activeTags.add(tag);
            } else {
                activeTags.remove(tag);
            }
        }

        checkBox.setOnClickListener {
            if (tag != null) {
                if (checkBox.isChecked && !activeTags.contains(tag)) {
                    activeTags.add(tag);
                } else {
                    activeTags.remove(tag);
                }
            }
        }

        checkBox.text = tag?.name.toString()

        return convertView ?: cv
    }

    fun resetTags() {
        activeTags.clear()
        notifyDataSetChanged()
    }

    fun activeTags(): ArrayList<MangaTag> {
        return activeTags
    }

}