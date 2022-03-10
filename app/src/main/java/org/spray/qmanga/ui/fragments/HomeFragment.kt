package org.spray.qmanga.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import org.spray.qmanga.client.models.*
import org.spray.qmanga.client.source.Source
import org.spray.qmanga.client.source.SourceManager
import org.spray.qmanga.databinding.FragmentHomeBinding
import org.spray.qmanga.ui.impl.mangalist.MangaListAdapter
import org.spray.qmanga.ui.impl.mangalist.MangaListViewFactory
import org.spray.qmanga.ui.impl.mangalist.MangaListViewModel

class HomeFragment : Fragment() {

    lateinit var mContext: Context

    lateinit var binding: FragmentHomeBinding
    lateinit var popularAdapter: MangaListAdapter
    lateinit var newestAdapter: MangaListAdapter

    var mDataPopular = arrayListOf<MangaData>()
    var mDataNewest = arrayListOf<MangaData>()

    lateinit var source: Source

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mContext = container?.context!!
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        source = SourceManager.get(MangaSource.REMANGA)

        popularAdapter = MangaListAdapter(mDataPopular,
            activity)
        newestAdapter = MangaListAdapter(mDataNewest,
            activity)

        val viewModel = ViewModelProvider(this, MangaListViewFactory(mContext, source))
            .get(MangaListViewModel::class.java)

        viewModel.popularList.observe(viewLifecycleOwner) {
            popularAdapter.setDataSet(it)
            popularAdapter.notifyDataSetChanged()
        }

        viewModel.newestList.observe(viewLifecycleOwner) {
            newestAdapter.setDataSet(it)
            newestAdapter.notifyDataSetChanged()
        }

        binding.apply {
            popularView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            newestView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

            popularView.adapter = popularAdapter
            newestView.adapter = newestAdapter
        }

        return binding.root
    }
/*
    private fun loadList(refresh: Boolean) {
        if (refresh) {
            mDataPopular.clear()
            mDataNewest.clear()
        }
        if (refresh || (mDataNewest.isEmpty() || mDataPopular.isEmpty())) {
            source.loadNewest(mContext, newestAdapter as MangaListAdapter, mDataNewest)
            source.loadPopular(mContext, popularAdapter as MangaListAdapter, mDataPopular)
        }
    }*/

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }

}