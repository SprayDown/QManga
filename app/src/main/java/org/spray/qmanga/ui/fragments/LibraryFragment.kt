package org.spray.qmanga.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import org.spray.qmanga.R
import org.spray.qmanga.databinding.FragmentLibraryBinding
import org.spray.qmanga.ui.base.BaseFragment
import org.spray.qmanga.ui.impl.library.DownloadsFragment
import org.spray.qmanga.ui.impl.library.RecentFragment
import org.spray.qmanga.ui.impl.page.PageFragmentAdapter

class LibraryFragment : BaseFragment(R.string.library), ElementsUpdateListener {

    private lateinit var binding: FragmentLibraryBinding

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLibraryBinding.inflate(inflater, container, false)
        val pageAdapter = PageFragmentAdapter(requireActivity())
        pageAdapter.add(RecentFragment())
        pageAdapter.add(DownloadsFragment())

        with(binding) {
            viewPager.adapter = pageAdapter
            viewPager.offscreenPageLimit = 2
            TabLayoutMediator(tabLayout, viewPager)
            { tab, position ->
                run {
                    when (position) {
                        0 -> tab.text = getString(R.string.recent)
                        1 -> tab.text = getString(R.string.downloads)
                    }
                }
            }.attach()
        }

        return binding.root
    }

    override fun onRecentUpdate(size: Int) {
    }
}

interface ElementsUpdateListener {
    fun onRecentUpdate(size: Int)
}