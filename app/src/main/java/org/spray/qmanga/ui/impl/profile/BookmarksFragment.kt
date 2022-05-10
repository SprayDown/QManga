package org.spray.qmanga.ui.impl.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.spray.qmanga.databinding.FragmentBookmarksBinding
import org.spray.qmanga.ui.base.BaseFragment
import org.spray.qmanga.ui.impl.mangalist.MangaCardAdapter
import org.spray.qmanga.utils.autoFitColumns

class BookmarksFragment(val listener: DataUpdateListener?) : BaseFragment() {

    private lateinit var binding: FragmentBookmarksBinding

    private var adapter: MangaCardAdapter? = null

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookmarksBinding.inflate(inflater, container, false)

        adapter = MangaCardAdapter(
            ArrayList(),
            requireActivity(),
            true
        )

        listener?.onUpdate(adapter!!)

        with(binding) {
            recyclerView.autoFitColumns(122)
            recyclerView.adapter = adapter

            swipeRefreshLayout.setOnRefreshListener {
                listener?.onUpdate(adapter!!)
                swipeRefreshLayout.isRefreshing = false
            }
        }

        return binding.root
    }

    interface DataUpdateListener {
        fun onUpdate(adapter: MangaCardAdapter)
    }
}