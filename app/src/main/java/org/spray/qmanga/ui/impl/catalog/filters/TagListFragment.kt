package org.spray.qmanga.ui.impl.catalog.filters

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import org.spray.qmanga.R
import org.spray.qmanga.client.models.MangaTag
import org.spray.qmanga.databinding.FragmentTagListBinding
import org.spray.qmanga.ui.base.BaseFragment
import kotlin.properties.Delegates


class TagListFragment(
) : BaseFragment() {

    private val args: TagListFragmentArgs by navArgs()

    private var stringId by Delegates.notNull<Int>()
    private lateinit var tags: Array<MangaTag>
    private lateinit var activeTags: Array<MangaTag>

    private lateinit var tagType: String

    private lateinit var binding: FragmentTagListBinding
    private lateinit var tagsAdapter: TagsAdapter<MangaTag>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        tags = args.tagList
        tagType = args.typeName
        stringId = args.stringId
        activeTags = args.activeTags

        if (stringId != -1)
            actionBar?.title = getString(stringId)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tagsbar_menu, menu)
    }

    override fun createView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTagListBinding.inflate(inflater, container, false)

        tagsAdapter = TagsAdapter(
            requireContext(),
            R.layout.item_filter_checkbox,
            tags,
            activeTags
        )

        binding.apply {
            listView.adapter = tagsAdapter
        }

        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.item_Reset) {
            tagsAdapter.resetTags()
            requireActivity().onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDetach() {
        super.onDetach()
        setFragmentResult(
            "parent_filters_key",
            bundleOf(
                tagType to tagsAdapter.activeTags(),
            )
        )
    }
}