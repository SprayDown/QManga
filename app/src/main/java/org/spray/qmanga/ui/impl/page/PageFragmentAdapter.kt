package org.spray.qmanga.ui.impl.page

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.util.*


class PageFragmentAdapter(
    fm: FragmentActivity
) : FragmentStateAdapter(fm) {

    private val fragments = arrayListOf<Fragment>()

    fun add(fragment: Fragment) {
        fragments.add(fragment)
    }

    fun firstToLast() {
        Collections.rotate(fragments, -1)
    }

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int) = fragments[position]
}