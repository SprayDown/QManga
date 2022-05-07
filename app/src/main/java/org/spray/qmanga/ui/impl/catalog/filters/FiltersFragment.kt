package org.spray.qmanga.ui.impl.catalog.filters

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.spray.qmanga.R
import org.spray.qmanga.client.models.MangaTag
import org.spray.qmanga.client.models.QueryData
import org.spray.qmanga.client.models.TagType
import org.spray.qmanga.client.source.Source
import org.spray.qmanga.client.source.SourceManager
import org.spray.qmanga.databinding.FragmentFiltersBinding
import org.spray.qmanga.ui.base.BaseFragment


class FiltersFragment : BaseFragment() {

    private lateinit var binding: FragmentFiltersBinding
    private lateinit var statusAdapter: TagsAdapter<MangaTag>
    private lateinit var limitAdapter: TagsAdapter<MangaTag>

    private lateinit var adapter: SectionedAdapter

    private val args: FiltersFragmentArgs by navArgs()
    private lateinit var queryData: QueryData

    private var genreList: List<MangaTag>? = null
    private var categoryList: List<MangaTag>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        queryData = args.queryData

        parentFragmentManager.setFragmentResultListener("parent_filters_key", this) { _, bundle ->
            val parseGenres =
                bundle.getParcelableArrayList<MangaTag>(TagType.GENRE.name.lowercase())

            if (parseGenres != null)
                genreList = parseGenres

            val parseCategories =
                bundle.getParcelableArrayList<MangaTag>(TagType.CATEGORY.name.lowercase())

            if (parseCategories != null)
                categoryList = parseCategories

            setViewValues(SourceManager.getCurrentSource())
        }

        val source = SourceManager.getCurrentSource()

        statusAdapter = TagsAdapter(
            requireContext(),
            R.layout.item_filter_checkbox,
            source.tags.filter { it.type == TagType.STATUS }.toTypedArray(),
            queryData.status?.toTypedArray() ?: emptyArray()
        )
        limitAdapter = TagsAdapter(
            requireContext(),
            R.layout.item_filter_checkbox,
            source.tags.filter { it.type == TagType.LIMIT }.toTypedArray(),
            queryData.limit?.toTypedArray() ?: emptyArray()
        )
    }

    override fun createView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFiltersBinding.inflate(inflater, container, false)
        adapter = SectionedAdapter(requireContext())

        setViewValues(SourceManager.getCurrentSource())

        adapter.addSection(getString(TagType.STATUS.id), statusAdapter);
        adapter.addSection(getString(TagType.LIMIT.id), limitAdapter);

        binding.listView.adapter = adapter
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    fun setViewValues(source: Source) = with(binding) {
        genres.textView.text = getString(R.string.genres)
        val activeGenres = (genreList?.size
            ?: if (queryData.genres != null) queryData.genres!!.size else 0)
        genres.textView2.text =
            if (activeGenres > 0) activeGenres.toString() + " " + getString(R.string.selected) else getString(
                R.string.any
            )
        genres.frame.setOnClickListener {
            val action = FiltersFragmentDirections.actionFiltersFragmentToTagListFragment(
                tagList = source.tags.filter { it.type == TagType.GENRE }.toTypedArray(),
                typeName = TagType.GENRE.name.lowercase(),
                stringId = R.string.genres,
                activeTags = genreList?.toTypedArray() ?: queryData.genres?.toTypedArray()
                ?: emptyArray()
            )
            findNavController().navigate(action)
        }

        categories.textView.text = getString(R.string.categories)
        val activeCategories = (categoryList?.size
            ?: if (queryData.categories != null) queryData.categories!!.size else 0)
        categories.textView2.text =
            if (activeCategories > 0) activeCategories.toString() + " " + getString(R.string.selected) else getString(
                R.string.any
            )
        categories.frame.setOnClickListener {

            val action = FiltersFragmentDirections.actionFiltersFragmentToTagListFragment(
                tagList = source.tags.filter { it.type == TagType.CATEGORY }.toTypedArray(),
                typeName = TagType.CATEGORY.name.lowercase(),
                stringId = R.string.categories,
                activeTags = categoryList?.toTypedArray() ?: queryData.categories?.toTypedArray()
                ?: emptyArray()
            )

            findNavController().navigate(action)
        }
    }

    override fun onDetach() {
        super.onDetach()
        if (genreList == null && queryData.genres != null)
            genreList = queryData.genres

        if (categoryList == null && queryData.categories != null)
            categoryList = queryData.categories

        setFragmentResult(
            "filters_key",
            bundleOf(
                "status_key" to statusAdapter.activeTags(),
                "limit_key" to limitAdapter.activeTags(),
                "genres_key" to genreList,
                "category_key" to categoryList
            )
        )
    }

}