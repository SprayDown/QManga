package org.spray.qmanga.ui.reader.chapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomappbar.BottomAppBar
import org.spray.qmanga.R
import org.spray.qmanga.client.models.MangaChapter
import org.spray.qmanga.databinding.FragmentChaptersReaderBinding
import org.spray.qmanga.ui.base.listener.OnItemClickListener
import org.spray.qmanga.ui.reader.ReaderActivity
import org.spray.qmanga.utils.ext.forceShowBar

class ReaderChapterFragment(
    private val chapters: List<MangaChapter>,
    var currentChapter: MangaChapter,
    private val bottomBar: BottomAppBar
) :
    Fragment() {

    private lateinit var binding: FragmentChaptersReaderBinding
    private lateinit var mContext: Context

    private lateinit var toolbar: Toolbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mContext = container?.context!!
        binding = FragmentChaptersReaderBinding.inflate(inflater, container, false)

        val adapter = ReaderChapterAdapter(chapters.toList(), activity,
            currentChapter, object : OnItemClickListener<MangaChapter> {
                override fun onItemClick(item: MangaChapter) {
                    (requireActivity() as ReaderActivity).updateChapter(item)
                }

                override fun onLongClick(item: MangaChapter) {
                }
            })

        binding.apply {
            recyclerViewChapters.layoutManager = LinearLayoutManager(context);

            recyclerViewChapters.adapter = adapter

            recyclerViewChapters.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        bottomBar.forceShowBar()
                    }
                }
            })

            closeButton.setOnClickListener {
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        toolbar = requireActivity().findViewById(R.id.toolbar)
        toolbar.updateLayoutParams<AppBarLayout.LayoutParams> {
            scrollFlags =
                AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL
        }
    }

    override fun onDetach() {
        super.onDetach()
        toolbar.updateLayoutParams<AppBarLayout.LayoutParams> {
            scrollFlags =
                AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
        }
    }
}