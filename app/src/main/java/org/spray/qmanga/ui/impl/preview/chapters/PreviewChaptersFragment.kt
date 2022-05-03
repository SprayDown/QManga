package org.spray.qmanga.ui.impl.preview.chapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import org.spray.qmanga.R
import org.spray.qmanga.client.models.ListType
import org.spray.qmanga.client.models.MangaChapter
import org.spray.qmanga.client.models.MangaData
import org.spray.qmanga.databinding.FragmentChaptersBinding
import org.spray.qmanga.ui.base.listener.OnChapterClickListener
import org.spray.qmanga.ui.impl.library.download.DownloadManager
import org.spray.qmanga.ui.impl.library.download.DownloadService
import org.spray.qmanga.ui.reader.ReaderActivity

class PreviewChaptersFragment(val data: MangaData) : Fragment() {

    private lateinit var binding: FragmentChaptersBinding
    private lateinit var mContext: Context

    private var order = ListType.DESCENDING

    var adapter: PreviewChaptersAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mContext = requireContext()
        binding = FragmentChaptersBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PreviewChaptersAdapter(ArrayList(), activity, object : OnChapterClickListener {
            override fun onChapterClick(chapter: MangaChapter) {
                val intent = Intent(activity, ReaderActivity::class.java).apply {
                    putExtra("data", data)
                    putExtra("chapter", chapter)
                    putParcelableArrayListExtra("chapters", ArrayList(adapter?.getDataSet()))
                }
                requireActivity().startActivity(intent)
            }

            override fun onDownloadClick(chapter: MangaChapter) {
                if (!DownloadManager.checkPermission(requireActivity()))
                    return

                val bundle = Bundle().apply {
                    putParcelable("manga_data", data)
                    putParcelable("manga_chapter", chapter)
                }
                val intent = Intent(requireActivity(), DownloadService::class.java).apply {
                    putExtra("bundle", bundle)
                }
                requireActivity().startService(intent)
            }

        })

        val linearLayoutManager =
            LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                order == ListType.DESCENDING
            ).apply {
                stackFromEnd = order == ListType.DESCENDING
            }

        with(binding) {
            linearLayoutOrder.setOnClickListener {
                order =
                    if (order == ListType.DESCENDING) ListType.ASCENDING else ListType.DESCENDING
                val toAngle = if (order == ListType.DESCENDING) 0f else 180f
                imageViewOrder.animate().rotation(toAngle).start()

                linearLayoutManager.apply {
                    reverseLayout = order == ListType.DESCENDING
                    stackFromEnd = order == ListType.DESCENDING
                }
                recyclerView.layoutManager = linearLayoutManager
            }

            recyclerView.layoutManager = linearLayoutManager
            val itemDecoration =
                DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            itemDecoration.setDrawable(activity?.getDrawable(R.drawable.divider_line)!!)
            recyclerView.addItemDecoration(itemDecoration)

            recyclerView.adapter = adapter
        }
    }

    fun getChapter(id: Long): MangaChapter {
        return getChapters().first { it.id == id }
    }

    fun getChapters(): List<MangaChapter> {
        return adapter?.getDataSet() ?: emptyList()
    }

    fun setDataSet(data: List<MangaChapter>) {
        binding.progressBar.visibility = View.GONE
        adapter?.setDataSet(data)
    }
}