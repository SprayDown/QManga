package org.spray.qmanga.ui.impl.search

import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import org.spray.qmanga.R
import org.spray.qmanga.client.source.SourceManager
import org.spray.qmanga.databinding.ActivitySearchBinding
import org.spray.qmanga.ui.base.BaseActivity
import org.spray.qmanga.ui.impl.mangalist.MangaListAdapter

class SearchActivity : BaseActivity<ActivitySearchBinding>() {

    private lateinit var searchAdapter: MangaListAdapter
    private lateinit var toolbar: MaterialToolbar

    lateinit var viewModel: SearchViewModel

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivitySearchBinding.inflate(layoutInflater))
        toolbar = findViewById(R.id.toolbar)
        supportActionBar?.hide()
        toolbar.title = getString(R.string.search)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val searchView = toolbar.findViewById(R.id.searchView) as SearchView
        searchView.requestFocus()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                search(query)
                searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                search(newText)
                return false
            }
        })

        val source = SourceManager.getCurrentSource()

        searchAdapter = MangaListAdapter(ArrayList(), this)

        viewModel = ViewModelProvider(this, SearchViewFactory(applicationContext, source))
            .get(SearchViewModel::class.java)

        binding.apply {
            recyclerView.layoutManager =
                LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)

            recyclerView.adapter = searchAdapter

            recyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    return false
                }

                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
                    searchView.clearFocus()
                }

                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
                }

            })
        }
    }

    private fun search(query: String) {
        if (query.isEmpty())
            return

        viewModel.search(query)

        viewModel.searchList.observe(this) {
            searchAdapter.setDataSet(ArrayList(it))
            binding.recyclerView.layoutManager?.scrollToPosition(0)
        }
    }
}