package org.spray.qmanga.ui.reader

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import org.spray.qmanga.R
import org.spray.qmanga.client.models.MangaChapter
import org.spray.qmanga.client.models.MangaData
import org.spray.qmanga.client.models.MangaRecent
import org.spray.qmanga.client.models.MangaSource
import org.spray.qmanga.client.source.Source
import org.spray.qmanga.client.source.SourceManager
import org.spray.qmanga.databinding.ReaderChapterBinding
import org.spray.qmanga.sqlite.query.ChapterQuery
import org.spray.qmanga.sqlite.query.RecentQuery
import org.spray.qmanga.ui.base.BaseActivity
import org.spray.qmanga.ui.base.listener.ReaderListener
import org.spray.qmanga.ui.reader.chapters.ReaderChapterFragment
import org.spray.qmanga.utils.ext.forceShowBar


class ReaderActivity : BaseActivity<ReaderChapterBinding>() {

    lateinit var chapter: MangaChapter
    private lateinit var source: Source

    private lateinit var adapter: ReaderAdapter
    lateinit var viewModel: ReaderViewModel

    private lateinit var toolbar: Toolbar

    private var chapters: ArrayList<MangaChapter>? = null
    private var mangaData: MangaData? = null

    private val recentQuery = RecentQuery()
    lateinit var chapterQuery: ChapterQuery

    private var read = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ReaderChapterBinding.inflate(layoutInflater))

        toolbar = findViewById(R.id.toolbar)
        toolbar.updateLayoutParams<AppBarLayout.LayoutParams> {
            scrollFlags =
                AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
        }
        supportActionBar?.hide()
        toolbar.title = getString(R.string.app_name)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        hideSystemBars()

        mangaData = intent.getParcelableExtra("data")
        chapterQuery = mangaData?.hashId?.let { ChapterQuery(it) }!!

        chapter = intent.getParcelableExtra<MangaChapter>("chapter") as MangaChapter

        chapters =
            intent.getParcelableArrayListExtra("chapters")

        source = SourceManager.get(MangaSource.REMANGA)
        adapter = ReaderAdapter(ArrayList(), this)
        viewModel =
            ViewModelProvider(this, ReaderViewFactory(source, chapter))
                .get(ReaderViewModel::class.java)

        if (chapters == null && mangaData != null) {
            binding.textViewLoading.visibility = View.VISIBLE
            viewModel.loadChapters(mangaData!!)
        }

        binding.apply {
            recyclerView.layoutManager =
                LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
            recyclerView.adapter = adapter
//            recyclerView.setItemViewCacheSize(4);
//            recyclerView.isDrawingCacheEnabled = true;
//            recyclerView.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH;

            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                @SuppressLint("SetTextI18n")
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val currentPage =
                        adapter.getDataSet()[(binding.recyclerView.layoutManager as LinearLayoutManager)
                            .findFirstVisibleItemPosition()].page
                    val lastPage = adapter.getDataSet()
                        .last().page

                    bottomBar.textViewPage.text = "$currentPage/$lastPage"

                    if (currentPage >= lastPage)
                        read = true

                    super.onScrolled(recyclerView, dx, dy)
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        bottomBar.bottomAppBar.forceShowBar()
                    }
                }
            })
        }

        initAppBar(chapter)

        if (chapters != null) {
            chapters!!.filter { !it.locked }
            initBottomBar(chapters!!)
        } else {
            viewModel.mChapters.observe(this) {
                if (it != null) {
                    binding.textViewLoading.visibility = View.GONE

                    val arrayList = ArrayList(it)
                    initBottomBar(arrayList)
                    chapters = arrayList
                }
            }
        }
    }

/*
    fun getListener(): ReaderListener {
        val lastPage = adapter.getDataSet()
            .last().page
        return object: ReaderListener {
            override fun onPageChanged(currentPage: Int) {
                binding.bottomBar.textViewPage.text = "$currentPage/$lastPage"
            }

            override fun forceShowBar() {
                binding.bottomBar.bottomAppBar.forceShowBar()
            }

            override fun onRead() {
                read = true
            }

        }
    }
*/

    override fun onResume() {
        super.onResume()
        read = false
    }

    override fun onPause() {
        super.onPause()
        if (read) {
            chapter.read = true
            chapterQuery.createOrUpdate(chapter, chapter.id.toInt(), null)
        }

        updateRecent()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    private fun hideSystemBars() {
        val windowInsetsController =
            ViewCompat.getWindowInsetsController(window.decorView) ?: return
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    private fun initAppBar(chapter: MangaChapter) = with(binding.appbarReader) {
        textViewTome.text = "?????? " + chapter.tome
        textViewChapter.text = "?????????? " + chapter.number
    }

    private fun initBottomBar(chapters: ArrayList<MangaChapter>) = with(binding.bottomBar) {
        updateChapter(chapter)

        buttonNext.setOnClickListener {
            if (chapters.indexOf(chapter) < chapters.size - 1) {
                updateChapter(chapters[chapters.indexOf(chapter) + 1])
            }
        }
        buttonPrev.setOnClickListener {
            if (chapters.indexOf(chapter) > 0) {
                updateChapter(chapters[chapters.indexOf(chapter) - 1])
            }
        }

        buttonChapters.setOnClickListener {
            if (supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.popBackStack()
            } else {
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.slideup_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.slideup_out
                    )
                    .replace(
                        R.id.fragmentContainerView,
                        ReaderChapterFragment(chapters, chapter, bottomAppBar)
                    )
                    .addToBackStack("chapters")
                    .commit();
            }
        }
    }

    private fun addRecent(mData: MangaRecent) {
        recentQuery.createOrUpdate(mData, mData.hashId, null)
    }

    fun updateChapter(chapter: MangaChapter) {
        viewModel.chapter = chapter
        viewModel.loadPages()

        viewModel.mPages.observe(this, adapter::setDataSet)
        binding.recyclerView.layoutManager?.scrollToPosition(0)

        this.chapter = chapter

        initAppBar(chapter)
        chapters?.let { updateLocked(it) }
    }

    private fun updateRecent() {
        mangaData?.let {
            addRecent(
                MangaRecent(
                    name = it.name,
                    imageUrl = it.imageUrl,
                    url = it.url,
                    type = it.type,
                    rating = it.rating,
                    id = recentQuery.getId(it.hashId).toLong(),
                    position = 0,
                    chapterId = chapter.id,
                    chapterTome = chapter.tome,
                    chapterNumber = chapter.number
                )
            )
        }
    }

    private fun updateLocked(chapters: ArrayList<MangaChapter>) = with(binding.bottomBar) {
        val lockedColor = resources.getColor(R.color.closed_gray)
        val unlockedColor = resources.getColor(R.color.white)
        buttonPrev.setColorFilter(
            if (chapters.indexOf(chapter) <= 0) lockedColor else unlockedColor
        )
        buttonNext.setColorFilter(
            if (chapters.indexOf(chapter) >= chapters.size - 1) lockedColor else unlockedColor
        )
    }
}