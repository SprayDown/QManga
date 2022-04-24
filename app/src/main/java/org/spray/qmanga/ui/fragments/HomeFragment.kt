package org.spray.qmanga.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.ImageScaleType
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import org.spray.qmanga.R
import org.spray.qmanga.client.models.MangaChapter
import org.spray.qmanga.client.source.Source
import org.spray.qmanga.client.source.SourceManager
import org.spray.qmanga.databinding.FragmentHomeBinding
import org.spray.qmanga.sqlite.QueryResponse
import org.spray.qmanga.sqlite.models.MangaRecent
import org.spray.qmanga.sqlite.query.RecentQuery
import org.spray.qmanga.ui.base.BaseFragment
import org.spray.qmanga.ui.impl.mangalist.MangaCardAdapter
import org.spray.qmanga.ui.impl.mangalist.MangaListViewFactory
import org.spray.qmanga.ui.impl.mangalist.MangaListViewModel
import org.spray.qmanga.ui.impl.preview.PreviewActivity
import org.spray.qmanga.ui.impl.search.SearchActivity
import org.spray.qmanga.ui.reader.ReaderActivity
import org.spray.qmanga.utils.ext.blurRenderScript

class HomeFragment : BaseFragment() {

    lateinit var mContext: Context

    private lateinit var binding: FragmentHomeBinding
    private lateinit var popularAdapter: MangaCardAdapter
    private lateinit var newestAdapter: MangaCardAdapter
    lateinit var source: Source
    lateinit var viewModel: MangaListViewModel

    private var options: DisplayImageOptions = DisplayImageOptions.Builder()
        .showImageOnLoading(R.drawable.empty)
        .resetViewBeforeLoading(true)
        .cacheOnDisk(true)
        .cacheInMemory(true)
        .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
        .bitmapConfig(Bitmap.Config.RGB_565)
        .considerExifParams(true)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        source = SourceManager.getCurrentSource()

        viewModel = ViewModelProvider(this, MangaListViewFactory(requireContext(), source))
            .get(MangaListViewModel::class.java)

        popularAdapter = MangaCardAdapter(
            ArrayList(),
            activity
        )
        newestAdapter = MangaCardAdapter(
            ArrayList(),
            activity
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.homebar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_Search -> {
                val intent = Intent(activity, SearchActivity::class.java)
                requireActivity().startActivity(intent)
                requireActivity().overridePendingTransition(0, 0)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun createView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mContext = requireContext()
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        update()

        with(binding) {
            popularView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            newestView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

            swipeRefreshLayout.setOnRefreshListener {
                update()
                swipeRefreshLayout.isRefreshing = false
            }

            popularView.adapter = popularAdapter
            newestView.adapter = newestAdapter

            OverScrollDecoratorHelper.setUpOverScroll(
                popularView,
                OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL
            )
            OverScrollDecoratorHelper.setUpOverScroll(
                newestView,
                OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL
            )
        }

        viewModel.popularList.observe(viewLifecycleOwner, popularAdapter::setDataSet)

        viewModel.newestList.observe(viewLifecycleOwner, newestAdapter::setDataSet)

        return binding.root
    }

    override fun onResume() {
        initFastStart()
        super.onResume()
    }

    private fun initFastStart() = with(binding) {
        val query = RecentQuery()

        query.getLast(object : QueryResponse<MangaRecent> {
            override fun onSuccess(data: MangaRecent) {
                layoutFastStart.visibility = View.VISIBLE
                textViewManga.text = data.name
                buttonRead.text =
                    getString(R.string.continue_read) + " Том " + data.chapterTome + " Гл." + data.chapterNumber

                ImageLoader.getInstance()
                    .loadImage(data.imageUrl, options, object : SimpleImageLoadingListener() {
                        override fun onLoadingComplete(
                            imageUri: String?,
                            view: View?,
                            loadedImage: Bitmap?
                        ) {
                            loadedImage ?: return

                            val roundedDrawable =
                                RoundedBitmapDisplayer.RoundedDrawable(loadedImage, 14, 0)
                            val bgDrawable =
                                BitmapDrawable(
                                    loadedImage.blurRenderScript(
                                        requireContext(),
                                        18
                                    )
                                ).apply {
                                    alpha = 145
                                }

                            imageViewChildManga.setImageDrawable(roundedDrawable)
                            imageViewBgManga.setImageDrawable(bgDrawable)
                        }
                    })

                layoutFastStart.setOnClickListener {
                    val intent = Intent(activity, PreviewActivity::class.java).apply {
                        putExtra("manga_data", data)
                    }
                    requireActivity().startActivity(intent)
                }

                buttonRead.setOnClickListener {
                    val intent = Intent(requireContext(), ReaderActivity::class.java).apply {
                        putExtra("data", data)
                        putExtra(
                            "chapter",
                            MangaChapter(
                                data.chapterId,
                                String(),
                                data.chapterTome,
                                data.chapterNumber,
                                null
                            )
                        )
                    }
                    startActivity(intent)
                }
            }

            override fun onFailure(msg: String) {
                layoutFastStart.visibility = View.GONE
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun update() {
        viewModel.loadList()
        initFastStart()
    }
}