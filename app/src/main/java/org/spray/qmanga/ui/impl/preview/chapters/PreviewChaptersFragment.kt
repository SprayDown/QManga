package org.spray.qmanga.ui.impl.preview.chapters

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import org.spray.qmanga.client.models.ListType
import org.spray.qmanga.client.models.MangaChapter
import org.spray.qmanga.client.models.MangaData
import org.spray.qmanga.databinding.FragmentChaptersBinding
import org.spray.qmanga.ui.base.listener.OnChapterClickListener
import org.spray.qmanga.ui.impl.library.download.DownloadManager
import org.spray.qmanga.ui.impl.library.download.DownloadService
import org.spray.qmanga.ui.impl.preview.PreviewViewModel
import org.spray.qmanga.ui.reader.ReaderActivity

class PreviewChaptersFragment(val data: MangaData, val viewModel: PreviewViewModel) : Fragment() {

    private lateinit var binding: FragmentChaptersBinding
    private lateinit var mContext: Context

    private var order = ListType.DESCENDING

    var adapter: PreviewChaptersAdapter? = null

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val queued: Boolean? = intent?.getBooleanExtra("queued", false)
            val chapterId = intent?.getLongExtra("chapter_id", -1)

            if (queued != null)
                viewModel.onDownloadQueued(chapterId?.let { getChapter(it) }, queued)

            chapterId ?: return

            val downloaded = intent.getBooleanExtra("downloaded", false)
            val chapter = getChapter(chapterId)
            if (chapter != null && downloaded)
                viewModel.onDownloadComplete(chapter)

            getChapters()
                .indexOf(chapter)
                .let { itChapter -> adapter?.notifyItemChanged(itChapter) }
        }
    }

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

    override fun onResume() {
        super.onResume()
        requireActivity().registerReceiver(
            broadcastReceiver,
            IntentFilter(DownloadService.CHAPTER_DOWNLOAD_ACTION)
        )
    }

    override fun onPause() {
        requireActivity().unregisterReceiver(broadcastReceiver)
        super.onPause()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PreviewChaptersAdapter(activity, viewModel, object : OnChapterClickListener {
            override fun onChapterClick(chapter: MangaChapter) {
                if (chapter.locked)
                    return

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
/*            val itemDecoration =
                DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            itemDecoration.setDrawable(activity?.getDrawable(R.drawable.divider_line)!!)
            recyclerView.addItemDecoration(itemDecoration)*/

            recyclerView.adapter = adapter
        }
    }

    fun getChapter(id: Long): MangaChapter? {
        return getChapters().firstOrNull { it.id == id }
    }

    fun getChapters(): List<MangaChapter> {
        return adapter?.getDataSet() ?: emptyList()
    }

    fun setDataSet(data: List<MangaChapter>) {
        binding.progressBar.visibility = View.GONE
        adapter?.setDataSet(data)
    }
}