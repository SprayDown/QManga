package org.spray.qmanga.ui.impl.page

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class PageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val fragments = arrayListOf<FragmentForm>()

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position].fragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragments[position].title
    }

    open fun addFragment(title: String, fragment: Fragment) {
        fragments.add(FragmentForm(title, fragment))
    }


    data class FragmentForm(val title: String, val fragment: Fragment)

}