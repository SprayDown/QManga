package org.spray.qmanga.ui.impl.preview

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import org.spray.qmanga.R
import org.spray.qmanga.client.models.MangaData
import org.spray.qmanga.client.models.MangaDetails
import org.spray.qmanga.client.models.MangaSource
import org.spray.qmanga.client.source.Source
import org.spray.qmanga.client.source.SourceManager
import org.spray.qmanga.databinding.FragmentPreviewBinding
import org.spray.qmanga.ui.impl.page.PageAdapter
import org.spray.qmanga.ui.impl.preview.chapters.ChaptersFragment
import org.spray.qmanga.ui.impl.preview.details.DetailsFragment
import org.spray.qmanga.utils.prettyCount

class PreviewFragment(private val data: MangaData) : Fragment() {

    private lateinit var binding: FragmentPreviewBinding
    private lateinit var mContext: Context

    private var bottomNav: View? = null

    lateinit var source: Source

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mContext = container?.context!!
        binding = FragmentPreviewBinding.inflate(inflater, container, false)
        source = SourceManager.get(MangaSource.REMANGA)

        val viewModel = ViewModelProvider(this, PreviewModelFactory(mContext, source, data))
            .get(PreviewViewModel::class.java)

        viewModel.mDetails.observe(viewLifecycleOwner, ::initDetails)

        return binding.root
    }

    private fun initDetails(details: MangaDetails) {
        Glide.with(this).asBitmap().load(details.previewImgUrl).into(binding.imageViewPreview)
        binding.apply {
            textViewName.text = details.data.name
            textViewRating.text = details.avg_rating.toString()
            (if (details.count_rating != null) "(голосов " + details.count_rating.toString() + ")" else "").also {
                textViewVoices.text = it
            }
            textViewYear.text = details.issue_year
            textViewMtype.text = details.data.type
            textViewViews.text = details.total_views.toString().prettyCount()
        }

        val viewPager = binding.viewpager
        val pageAdapter = PageAdapter(childFragmentManager)

        pageAdapter.addFragment("Информация", DetailsFragment(details))
        pageAdapter.addFragment("Главы", ChaptersFragment(details))

        viewPager.adapter = pageAdapter
        val tabLayout = binding.tablayout
        tabLayout.setupWithViewPager(viewPager)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        bottomNav = activity?.findViewById(R.id.bottomNavigationView);
        bottomNav!!.visibility = View.GONE
    }

    override fun onDetach() {
        super.onDetach()
        bottomNav!!.visibility = View.VISIBLE
    }
}