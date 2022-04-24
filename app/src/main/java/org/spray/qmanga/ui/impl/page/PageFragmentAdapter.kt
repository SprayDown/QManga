package org.spray.qmanga.ui.impl.page

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import org.spray.qmanga.client.models.MangaData
import org.spray.qmanga.client.models.MangaDetails


class PageFragmentAdapter(
    fm: FragmentActivity
) : FragmentStateAdapter(fm) {

    val fragments = arrayListOf<Fragment>()

    fun add(fragment: Fragment) {
        fragments.add(fragment)
    }

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int) = fragments[position]
}