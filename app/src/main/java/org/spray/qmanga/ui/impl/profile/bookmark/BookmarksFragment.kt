package org.spray.qmanga.ui.impl.profile.bookmark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import org.spray.qmanga.R
import org.spray.qmanga.databinding.FragmentBookmarksBinding
import org.spray.qmanga.ui.base.BaseFragment
import org.spray.qmanga.ui.base.RecyclerDivider
import org.spray.qmanga.ui.base.listener.InfiniteScrollProvider

class BookmarksFragment(val listener: DataUpdateListener?) : BaseFragment() {

    private lateinit var binding: FragmentBookmarksBinding

    private var adapter: BookmarkListAdapter? = null

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookmarksBinding.inflate(inflater, container, false)

        adapter = BookmarkListAdapter(
            ArrayList(),
            requireActivity()
        )

        listener?.init(adapter!!)
        listener?.onUpdate(adapter!!)

        with(binding) {
            recyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            recyclerView.adapter = adapter

            recyclerView.addItemDecoration(RecyclerDivider(requireContext(), R.drawable.divider_line_gray, 150))

            val infiniteScroll = InfiniteScrollProvider()
            infiniteScroll.attach(recyclerView, 0)
            infiniteScroll.setOnLoadMoreListener { page ->
                listener?.loadMore(page, infiniteScroll)
            }

            swipeRefreshLayout.setOnRefreshListener {
                listener?.onUpdate(adapter!!)
                swipeRefreshLayout.isRefreshing = false
            }
        }

        return binding.root
    }

    interface DataUpdateListener {
        fun onUpdate(adapter: BookmarkListAdapter)
        fun loadMore(page: Int, scrollProvider: InfiniteScrollProvider)
        fun init(adapter: BookmarkListAdapter)
    }
}