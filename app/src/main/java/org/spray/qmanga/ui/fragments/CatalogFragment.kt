package org.spray.qmanga.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.shape.CornerFamily
import com.maxkeppeler.sheets.input.InputSheet
import com.maxkeppeler.sheets.input.type.InputRadioButtons
import org.spray.qmanga.R
import org.spray.qmanga.client.models.*
import org.spray.qmanga.client.source.Source
import org.spray.qmanga.client.source.SourceManager
import org.spray.qmanga.databinding.FragmentCatalogBinding
import org.spray.qmanga.ui.base.BaseFragment
import org.spray.qmanga.ui.impl.catalog.CatalogViewFactory
import org.spray.qmanga.ui.impl.catalog.CatalogViewModel
import org.spray.qmanga.ui.impl.mangalist.MangaCardAdapter
import org.spray.qmanga.utils.autoFitColumns

class CatalogFragment : BaseFragment(R.string.catalog_header) {

    private lateinit var binding: FragmentCatalogBinding
    private lateinit var adapter: MangaCardAdapter
    private lateinit var viewModel: CatalogViewModel

    private var mData = arrayListOf<MangaData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentFragmentManager.setFragmentResultListener("filters_key", this) { _, bundle ->
            val status = bundle.getParcelableArrayList<MangaTag>("status_key")
            val limit = bundle.getParcelableArrayList<MangaTag>("limit_key")
            val genres = bundle.getParcelableArrayList<MangaTag>("genres_key")
            val categories = bundle.getParcelableArrayList<MangaTag>("category_key")

            viewModel.queryData =
                QueryData(status = status, limit = limit, genres = genres, categories = categories)
            update()
        }
    }

    override fun createView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCatalogBinding.inflate(inflater, container, false)

        adapter = MangaCardAdapter(
            mData,
            activity,
            true
        )
        val source = SourceManager.get(MangaSource.REMANGA)

        viewModel = ViewModelProvider(this, CatalogViewFactory(source)).get(
            CatalogViewModel::class.java
        )
        viewModel.mangas.observe(viewLifecycleOwner, adapter::setDataSet)

        with(binding) {
            recyclerView.autoFitColumns(122)
            recyclerView.clipToPadding = false

            recyclerView.adapter = adapter

            swipeRefreshLayout.setOnRefreshListener {
                update()
                swipeRefreshLayout.isRefreshing = false
            }

            viewModel.loading.observe(viewLifecycleOwner) {
                swipeRefreshLayout.isRefreshing = it
            }

            buttonSort.setOnClickListener {
                showSortSheet(source)
            }

            buttonFilters.setOnClickListener {
                val action =
                    CatalogFragmentDirections.actionCatalogFragmentToFiltersFragment(viewModel.queryData)
                findNavController().navigate(action)
            }
        }

        return binding.root
    }

    private fun showSortSheet(source: Source) {
        val sortTypes: MutableList<String> = mutableListOf()
        source.sortTypes.forEach {
            sortTypes.add(getString(it.id))
        }

        val listSortTypes = mutableListOf(
            getString(ListType.DESCENDING.id),
            getString(ListType.ASCENDING.id)
        )
        val sortTitle = getString(R.string.sort_title)

        InputSheet().show(requireContext()) {
            title(sortTitle)
            displayButtons(false)
            cornerFamily(CornerFamily.CUT)

            with(InputRadioButtons() {
                required()
                options(sortTypes)
                selected(source.sortTypes.indexOfFirst {
                    it.id == viewModel.sortType?.id
                })

                changeListener { value ->
                    viewModel.sortType = source.sortTypes[value]
                    update()
                }
            })

            with(InputRadioButtons() {
                label(R.string.sort_ordering)
                required()
                options(listSortTypes)
                selected(if (viewModel.listType == ListType.DESCENDING) 0 else 1)

                changeListener { value ->
                    viewModel.listType = if (value == 0) ListType.DESCENDING else ListType.ASCENDING
                    update()
                }
            })
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun update() {
        viewModel.update()
    }

}