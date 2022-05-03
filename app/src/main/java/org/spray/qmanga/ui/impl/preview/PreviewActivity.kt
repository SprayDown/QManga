package org.spray.qmanga.ui.impl.preview

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.ImageScaleType
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener
import org.spray.qmanga.R
import org.spray.qmanga.client.models.MangaChapter
import org.spray.qmanga.client.models.MangaData
import org.spray.qmanga.client.models.MangaDetails
import org.spray.qmanga.client.models.MangaRecent
import org.spray.qmanga.client.source.Source
import org.spray.qmanga.client.source.SourceManager
import org.spray.qmanga.databinding.ActivityPreviewBinding
import org.spray.qmanga.sqlite.QueryResponse
import org.spray.qmanga.sqlite.query.RecentQuery
import org.spray.qmanga.ui.base.BaseActivity
import org.spray.qmanga.ui.impl.library.download.DownloadManager
import org.spray.qmanga.ui.impl.library.download.DownloadService
import org.spray.qmanga.ui.impl.page.PageFragmentAdapter
import org.spray.qmanga.ui.impl.preview.chapters.PreviewChaptersFragment
import org.spray.qmanga.ui.impl.preview.details.DetailsFragment
import org.spray.qmanga.ui.reader.ReaderActivity
import org.spray.qmanga.utils.ext.blurRenderScript
import org.spray.qmanga.utils.ext.forceShowBar
import kotlin.math.roundToInt

const val CHAPTER_DOWNLOADED_ACTION = "org.spray.qmanga.chapterDownloadAction"

class PreviewActivity : BaseActivity<ActivityPreviewBinding>() {

    private var options: DisplayImageOptions = DisplayImageOptions.Builder()
        .showImageOnLoading(null)
        .resetViewBeforeLoading(true)
        .cacheInMemory(true)
        .cacheOnDisk(true)
        .imageScaleType(ImageScaleType.EXACTLY)
        .bitmapConfig(Bitmap.Config.RGB_565)
        .considerExifParams(true)
        .build()

    lateinit var source: Source
    lateinit var viewModel: PreviewViewModel

    lateinit var mangaData: MangaData

    private var recent: MangaRecent? = null
    private var chaptersFragment: PreviewChaptersFragment? = null
    private var chapter: MangaChapter? = null

    private var details: MangaDetails? = null

    private val actionReceive: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i("qmanga", "received")
            val chapterId = intent?.getLongExtra("chapter_id", -1)
            chaptersFragment?.getChapters()
                ?.indexOf(chapterId?.let { chaptersFragment?.getChapter(it) })
                ?.let { chapter -> chaptersFragment?.adapter?.notifyItemChanged(chapter) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityPreviewBinding.inflate(layoutInflater))

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = String()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        source = SourceManager.getCurrentSource()

        mangaData = intent.getParcelableExtra<MangaData>("manga_data") as MangaData

        ImageLoader.getInstance()
            .loadImage(mangaData.imageUrl, options, object : SimpleImageLoadingListener() {
                @SuppressLint("ResourceAsColor")
                override fun onLoadingComplete(
                    imageUri: String?,
                    view: View?,
                    loadedImage: Bitmap?
                ) {
                    loadedImage ?: return
                    binding.imageViewPreview.setImageBitmap(
                        loadedImage.blurRenderScript(applicationContext, 3)
                    )
                }
            })

        viewModel =
            ViewModelProvider(this, PreviewModelFactory(source, mangaData))
                .get(PreviewViewModel::class.java)

        with(binding) {
            appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                val fadeAlpha = ((255 * (1.0f - verticalOffset / -appBarLayout.totalScrollRange)))
                linearLayoutRating.alpha = fadeAlpha
                imageViewPreview.imageAlpha = fadeAlpha.roundToInt()
            })

            collapsingToolbar.title = mangaData.name

            collapsingToolbar.setExpandedTitleColor(
                ContextCompat.getColor(
                    applicationContext,
                    android.R.color.transparent
                )
            )

            bottomBar.buttonRead.setOnClickListener {
                if (chapter == null) {
                    Toast.makeText(
                        applicationContext,
                        "Секундочку, загружаем главы...",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val intent = Intent(applicationContext, ReaderActivity::class.java).apply {
                        putExtra("data", mangaData)
                        putExtra("chapter", chapter)
                        if (chaptersFragment?.getChapters()?.isNotEmpty() == true)
                            putParcelableArrayListExtra(
                                "chapters",
                                ArrayList(chaptersFragment?.getChapters())
                            )
                    }
                    startActivity(intent)
                }
            }

            bottomBar.buttonFavorite.setOnClickListener {
                Toast.makeText(applicationContext, "В разработке...", Toast.LENGTH_SHORT).show()
            }

            bottomBar.buttonShare.setOnClickListener {
                Toast.makeText(applicationContext, "В разработке...", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.loading.observe(this, this::setProgressBar)

        viewModel.mDetails.observe(this)
        {
            details = it
            setDetails(it, mangaData)
        }

        registerReceiver(actionReceive, IntentFilter(CHAPTER_DOWNLOADED_ACTION))
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(actionReceive)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.previewbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->
                finish()

            R.id.item_Download ->
                if (details != null && DownloadManager.checkPermission(this))
                    chaptersFragment?.getChapters()?.let {
                        val bundle = Bundle().apply {
                            putParcelable("manga_data", mangaData)
                            putParcelableArrayList("manga_chapters", ArrayList(it))
                        }
                        val intent = Intent(this, DownloadService::class.java).apply {
                            putExtra("bundle", bundle)
                        }
                        startService(intent)
                    }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        initHistory(mangaData)
    }

    private fun setProgressBar(state: Boolean) {
        binding.progressBar.visibility = if (state) View.VISIBLE else View.INVISIBLE
    }

    private fun setDetails(details: MangaDetails, data: MangaData) {
        val imageLoader = ImageLoader.getInstance()
        imageLoader
            .loadImage(details.previewImgUrl, options, object : SimpleImageLoadingListener() {
                @SuppressLint("ResourceAsColor")
                override fun onLoadingComplete(
                    imageUri: String?,
                    view: View?,
                    loadedImage: Bitmap?
                ) {
                    if (loadedImage != null) {
                        val td = TransitionDrawable(
                            arrayOf(
                                binding.imageViewPreview.drawable,
                                BitmapDrawable(resources, loadedImage)
                            )
                        )

                        binding.imageViewPreview.setImageDrawable(td)
                        td.startTransition(130);
                    }
                }
            })

        binding.apply {
            textViewRating.text = details.avg_rating.toString()
            (if (details.count_rating != null) "(голосов: " + details.count_rating.toString() + ")" else String()).also {
                textViewVoices.text = it
            }
            imageView2.visibility = View.VISIBLE
        }

        chaptersFragment = PreviewChaptersFragment(data)

        val viewPager = binding.viewpager
        val pageAdapter = PageFragmentAdapter(this)

        pageAdapter.add(DetailsFragment(details))
        if (chaptersFragment != null)
            pageAdapter.add(chaptersFragment!!)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                if (!viewPager.canScrollVertically(1) && state == ViewPager2.SCROLL_STATE_IDLE)
                    binding.bottomBar.bottomAppBar.forceShowBar()
            }
        })

        viewPager.adapter = pageAdapter
        viewPager.offscreenPageLimit = 2
        val tabLayout = binding.tablayout
        TabLayoutMediator(tabLayout, viewPager)
        { tab, position ->
            run {
                when (position) {
                    0 -> tab.text = getString(R.string.information)
                    1 -> tab.text = getString(R.string.chapters)
                }
            }
        }.attach()

        viewModel.loadChapters(details)

        viewModel.mChapters.observe(this)
        {
            chaptersFragment?.setDataSet(it)

            if (recent == null)
                chapter = if (chaptersFragment?.getChapters()
                        ?.isNotEmpty() == true
                ) chaptersFragment?.getChapters()?.first() else null
            else
                setBottomBarValues(recent!!)
        }
    }

    private fun initHistory(data: MangaData) {
        val query = RecentQuery()
        query.read(data.hashId, object : QueryResponse<MangaRecent> {
            override fun onSuccess(data: MangaRecent) {
                recent = data
                setBottomBarValues(data)
            }

            override fun onFailure(msg: String) {
            }
        })
    }

    private fun setBottomBarValues(data: MangaRecent) = with(binding.bottomBar) {
        buttonRead.text =
            getString(R.string.continue_read) + " Том " + data.chapterTome + " Гл." + data.chapterNumber
        chapter = MangaChapter(
            data.chapterId, String(), data.chapterTome, data.chapterNumber, null
        )
    }

    companion object {
        val LOCAL_MANGA_ACTION = "org.spray.qmanga.LOCAL_MANGA_BROADCAST"
    }
}