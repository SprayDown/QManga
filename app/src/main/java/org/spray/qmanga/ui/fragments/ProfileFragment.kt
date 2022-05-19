package org.spray.qmanga.ui.fragments

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.maxkeppeler.sheets.info.InfoSheet
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.ImageScaleType
import org.spray.qmanga.R
import org.spray.qmanga.client.models.MangaBookmark
import org.spray.qmanga.client.models.user.User
import org.spray.qmanga.client.models.user.UserData
import org.spray.qmanga.client.session.SessionManager
import org.spray.qmanga.client.source.SourceManager
import org.spray.qmanga.databinding.FragmentProfileBinding
import org.spray.qmanga.sqlite.QueryResponse
import org.spray.qmanga.sqlite.WRITE_TIME
import org.spray.qmanga.sqlite.query.BookmarkQuery
import org.spray.qmanga.ui.base.BaseFragment
import org.spray.qmanga.ui.base.listener.InfiniteScrollProvider
import org.spray.qmanga.ui.impl.page.PageFragmentAdapter
import org.spray.qmanga.ui.impl.profile.ProfileModelFactory
import org.spray.qmanga.ui.impl.profile.ProfileViewModel
import org.spray.qmanga.ui.impl.profile.bookmark.BookmarkListAdapter
import org.spray.qmanga.ui.impl.profile.bookmark.BookmarksFragment
import org.spray.qmanga.ui.impl.profile.UserInfoBottomSheet
import org.spray.qmanga.ui.web.WebViewActivity

class ProfileFragment : BaseFragment() {

    private lateinit var binding: FragmentProfileBinding

    private lateinit var viewModel: ProfileViewModel

    private var userData: UserData? = null

    private var tabLayoutMediator: TabLayoutMediator? = null

    private val pageAdapter: PageFragmentAdapter by lazy {
        PageFragmentAdapter(requireActivity())
    }

    private var options: DisplayImageOptions = DisplayImageOptions.Builder()
        .showImageOnLoading(null)
        .resetViewBeforeLoading(true)
        .cacheInMemory(true)
        .cacheOnDisk(true)
        .imageScaleType(ImageScaleType.EXACTLY)
        .bitmapConfig(Bitmap.Config.RGB_565)
        .considerExifParams(true)
        .build()

    private var resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.loadData()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profilebar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_exit ->
                InfoSheet().show(requireActivity()) {
                    content("Вы хотите выйти из аккаунта?")
                    onNegative("Отмена") {

                    }
                    onPositive("Выйти") {

                    }
                }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        val source = SourceManager.getCurrentSource()
        viewModel =
            ViewModelProvider(this, ProfileModelFactory(source))
                .get(ProfileViewModel::class.java)
//        viewModel.loadData()

        pageAdapter.add(BookmarksFragment(object : BookmarksFragment.DataUpdateListener {
            override fun onUpdate(adapter: BookmarkListAdapter) {
                val query = BookmarkQuery()
                query.readAll("$WRITE_TIME DESC", object : QueryResponse<List<MangaBookmark>> {
                    override fun onSuccess(data: List<MangaBookmark>) {
                        adapter.setDataSet(data)
                    }

                    override fun onFailure(msg: String) {
                    }
                })
            }

            override fun loadMore(page: Int, scrollProvider: InfiniteScrollProvider) {

            }

            override fun init(adapter: BookmarkListAdapter) {
            }
        }))

        with(binding) {
            header.constraintUserBlock.setOnClickListener {
                if (!SessionManager.isAuthenticated())
                    startWeb()
            }

            viewPager.adapter = pageAdapter

            tabLayoutMediator = TabLayoutMediator(header.tabLayout, viewPager)
            { tab, position ->
                run {
                    when (position) {
                        0 -> tab.text = getString(R.string.local)
                    }
                }
            }.apply { attach() }
        }

        SessionManager.userData.observe(viewLifecycleOwner) {
            userData = it
            setHeader(it, true)
        }

        SessionManager.authenticated.observe(viewLifecycleOwner) {
            setHeader(userData, it)
            if (it && userData != null) {
                SessionManager.updateSession(
                    User(userData!!.id, userData!!.username),
                    SourceManager.getCurrentKey()
                )
                addPages()
            }
        }

        return binding.root
    }

    private fun addPages() {
        if (pageAdapter.itemCount >= 7)
            return

        for (i in 0 until 6)
            addPage(i)
        pageAdapter.firstToLast()
        with(binding) {
            viewPager.adapter = pageAdapter
            tabLayoutMediator = TabLayoutMediator(header.tabLayout, viewPager)
            { tab, position ->
                run {
                    tabLayoutMediator?.detach()

                    when (position) {
                        0 -> tab.text = getString(R.string.reading)
                        1 -> tab.text = getString(R.string.will_read)
                        2 -> tab.text = getString(R.string.read)
                        3 -> tab.text = getString(R.string.abandoned)
                        4 -> tab.text = getString(R.string.postponed)
                        5 -> tab.text = getString(R.string.not_interesting)
                        6 -> tab.text = getString(R.string.local)
                    }
                }
            }.apply { attach() }
        }
    }

    private fun addPage(type: Int) {
        pageAdapter.add(BookmarksFragment(object : BookmarksFragment.DataUpdateListener {
            override fun onUpdate(adapter: BookmarkListAdapter) {
                viewModel.loadBookmarks(type, 30, 1)
            }

            override fun loadMore(page: Int, scrollProvider: InfiniteScrollProvider) {
                viewModel.loadBookmarks(type, 30, page + 2)
            }

            override fun init(adapter: BookmarkListAdapter) {
                viewModel.getData(type).observe(viewLifecycleOwner, adapter::setDataSet)
            }
        }))
    }

    private fun setHeader(userData: UserData?, auth: Boolean) = with(binding.header) {
        if (auth && userData != null) {
            ImageLoader.getInstance().displayImage(userData.avatarUrl, imageViewAvatar, options)
            textViewUsername.text = userData.username
            textViewId.text = "ID: " + userData.id.toString()
            textViewId.visibility = View.VISIBLE

            linearLayoutInfo.setOnClickListener {
                val infoSheet = UserInfoBottomSheet(userData)
                infoSheet.show(parentFragmentManager, UserInfoBottomSheet.TAG)
            }

        } else {
            textViewId.visibility = View.GONE
            textViewUsername.text = getString(R.string.click_to_login)
        }
    }

    private fun startWeb() {
        val source = SourceManager.getCurrentSource()
        resultLauncher.launch(
            WebViewActivity.createIntent(
                requireContext(),
                source.domain,
                source.loginUrl,
                source.destroyUrl
            )
        )
    }
}