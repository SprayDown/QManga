package org.spray.qmanga.ui.impl.catalog.filters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import org.spray.qmanga.R

class SectionedAdapter(val context: Context) : BaseAdapter() {

    private val sections: MutableMap<String, Adapter> = LinkedHashMap()
    var headers: ArrayAdapter<String> = ArrayAdapter(context, R.layout.list_header)
    private val TYPE_SECTION_HEADER = 0

    open fun addSection(section: String, adapter: Adapter) {
        headers.add(section)
        sections[section] = adapter
    }

    override fun getItem(position: Int): Any? {
        var position = position
        for (section in sections.keys) {
            val adapter: Adapter? = sections[section]
            val size: Int = adapter!!.count + 1

            // check if position inside this section
            if (position == 0) return section
            if (position < size) return adapter.getItem(position - 1)

            // otherwise jump into next section
            position -= size
        }
        return null
    }

    override fun getCount(): Int {
        // total together all sections, plus one for each section header
        var total = 0
        for (adapter in sections.values) total += adapter.count + 1
        return total
    }

    override fun getViewTypeCount(): Int {
        // assume that headers count as one, then total all sections
        var total = 1
        for (adapter in sections.values) total += adapter.viewTypeCount
        return total
    }

    override fun getItemViewType(position: Int): Int {
        var position = position
        var type = 1
        for (section in sections.keys) {
            val adapter: Adapter? = sections[section]
            val size: Int = adapter!!.count + 1

            // check if position inside this section
            if (position == 0) return TYPE_SECTION_HEADER
            if (position < size) if (adapter != null) {
                return type + adapter.getItemViewType(position - 1)
            }

            // otherwise jump into next section
            position -= size
            type += adapter!!.viewTypeCount
        }
        return -1
    }

    open fun areAllItemsSelectable(): Boolean {
        return false
    }

    override fun isEnabled(position: Int): Boolean {
        return getItemViewType(position) != TYPE_SECTION_HEADER
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var position = position
        for ((sectionnum, section) in sections.keys.withIndex()) {
            val adapter: Adapter? = sections[section]
            val size: Int = adapter!!.count + 1

            if (position == 0) return headers!!.getView(sectionnum, convertView, parent!!)
            if (position < size) return adapter.getView(position - 1, convertView, parent)

            position -= size
        }
        return null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}