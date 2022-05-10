package org.spray.qmanga.ui.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.ImageScaleType
import org.spray.qmanga.R
import org.spray.qmanga.client.models.MangaData
import org.spray.qmanga.client.models.user.User
import org.spray.qmanga.client.models.user.UserData
import org.spray.qmanga.client.session.SessionManager
import org.spray.qmanga.client.source.SourceManager
import org.spray.qmanga.databinding.FragmentProfileBinding
import org.spray.qmanga.sqlite.QueryResponse
import org.spray.qmanga.sqlite.WRITE_TIME
import org.spray.qmanga.sqlite.query.BookmarkQuery
import org.spray.qmanga.ui.base.BaseFragment
import org.spray.qmanga.ui.impl.mangalist.MangaCardAdapter
import org.spray.qmanga.ui.impl.page.PageFragmentAdapter
import org.spray.qmanga.ui.impl.profile.BookmarksFragment
import org.spray.qmanga.ui.impl.profile.ProfileModelFactory
import org.spray.qmanga.ui.impl.profile.ProfileViewModel
import org.spray.qmanga.ui.web.WebViewActivity

class ProfileFragment : BaseFragment() {

    private lateinit var binding: FragmentProfileBinding

    private lateinit var viewModel: ProfileViewModel

    private var userData: UserData? = null

    private var options: DisplayImageOptions = DisplayImageOptions.Builder()
        .showImageOnLoading(null)
        .resetViewBeforeLoading(true)
        .cacheInMemory(true)
        .cacheOnDisk(true)
        .imageScaleType(ImageScaleType.EXACTLY)
        .bitmapConfig(Bitmap.Config.RGB_565)
        .considerExifParams(true)
        .build()

    var resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.loadData()
        }
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
        viewModel.loadData()

        val pageAdapter = PageFragmentAdapter(requireActivity())
        pageAdapter.add(BookmarksFragment(object : BookmarksFragment.DataUpdateListener {
            override fun onUpdate(adapter: MangaCardAdapter) {
                val query = BookmarkQuery()
                query.readAll("$WRITE_TIME DESC", object : QueryResponse<List<MangaData>> {
                    override fun onSuccess(data: List<MangaData>) {
                        adapter.setDataSet(data)
                    }

                    override fun onFailure(msg: String) {
                    }
                })
            }
        }))

        with(binding) {
            header.constraintUserBlock.setOnClickListener {
                if (!SessionManager.isAuthenticated())
                    startWeb()
            }

            viewPager.adapter = pageAdapter
            TabLayoutMediator(tabLayout, viewPager)
            { tab, position ->
                run {
                    when (position) {
                        0 -> tab.text = getString(R.string.local)
                        1 -> tab.text = getString(R.string.downloads)
                    }
                }
            }.attach()
        }

        viewModel.userData.observe(viewLifecycleOwner) {
            userData = it
            setHeader(it, true)
        }

        viewModel.authenticated.observe(viewLifecycleOwner) {
            setHeader(userData, it)
            if (it && userData != null)
                SessionManager.updateSession(
                    User(userData!!.id, userData!!.username),
                    SourceManager.getCurrentKey()
                )
        }

        return binding.root
    }

    private fun setHeader(userData: UserData?, auth: Boolean) = with(binding.header) {
        if (auth && userData != null) {
            ImageLoader.getInstance().displayImage(userData.avatarUrl, imageViewAvatar, options)
            textViewUsername.text = userData.username
            textViewId.text = userData.id.toString()
            textViewId.visibility = View.VISIBLE
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