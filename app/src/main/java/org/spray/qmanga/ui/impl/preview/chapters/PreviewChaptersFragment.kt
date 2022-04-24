package org.spray.qmanga.ui.impl.preview.chapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import org.spray.qmanga.R
import org.spray.qmanga.client.models.MangaChapter
import org.spray.qmanga.client.models.MangaData
import org.spray.qmanga.databinding.FragmentChaptersBinding

class PreviewChaptersFragment(val data: MangaData) : Fragment() {

    private lateinit var binding: FragmentChaptersBinding
    private lateinit var mContext: Context

    private var order = Order.DOWN

    var adapter: PreviewChaptersAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

        adapter = PreviewChaptersAdapter(ArrayList(), data, activity)

        val linearLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, order == Order.DOWN).apply {
                stackFromEnd = order == Order.DOWN
            }

        with(binding) {
            linearLayoutOrder.setOnClickListener {
                order = if (order == Order.DOWN) Order.UP else Order.DOWN
                val toAngle = if (order == Order.DOWN) 0f else 180f
                imageViewOrder.animate().rotation(toAngle).start()

                linearLayoutManager.apply {
                    reverseLayout = order == Order.DOWN
                    stackFromEnd = order == Order.DOWN
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

    fun getChapters(): List<MangaChapter> {
        return adapter?.getDataSet() ?: emptyList()
    }

    fun setDataSet(data: List<MangaChapter>) {
        binding.progressBar.visibility = View.GONE
        adapter?.setDataSet(data)
    }

    enum class Order {
        UP, DOWN
    }
}