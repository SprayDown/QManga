package org.spray.qmanga.ui.impl.preview.details

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.chip.Chip
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import org.spray.qmanga.R
import org.spray.qmanga.client.models.MangaDetails
import org.spray.qmanga.databinding.FragmentDetailsBinding
import org.spray.qmanga.ui.impl.mangalist.MangaCardAdapter
import org.spray.qmanga.ui.impl.preview.PreviewViewModel
import org.spray.qmanga.utils.ext.prettyCount
import org.spray.qmanga.utils.ext.themeColor


class DetailsFragment(
    private val details: MangaDetails,
    val viewModel: PreviewViewModel,
    val viewPager: ViewPager2
) :
    Fragment() {

    private lateinit var binding: FragmentDetailsBinding
    private lateinit var similarAdapter: MangaCardAdapter
    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mContext = requireContext()
        binding = FragmentDetailsBinding.inflate(inflater, container, false)

        addInfo(inflater)

        with(binding) {
            similarAdapter = MangaCardAdapter(
                ArrayList(),
                activity
            )

            similarView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            similarView.adapter = similarAdapter

            similarView.addOnItemTouchListener(object : OnItemTouchListener {
                var lastX = 0
                override fun onInterceptTouchEvent(
                    rv: RecyclerView,
                    e: MotionEvent
                ): Boolean {
                    when (e.action) {
                        MotionEvent.ACTION_DOWN -> lastX = e.x.toInt()
                        MotionEvent.ACTION_MOVE -> {
                            val isScrollingRight = e.x < lastX
                            viewPager.isUserInputEnabled =
                                isScrollingRight && (similarView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition() == similarAdapter.itemCount - 1 ||
                                        !isScrollingRight && (similarView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition() == 0
                        }
                        MotionEvent.ACTION_UP -> {
                            lastX = 0
                            viewPager.isUserInputEnabled = true
                        }
                    }
                    return false
                }

                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
            })

            OverScrollDecoratorHelper.setUpOverScroll(
                similarView,
                OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL
            )
        }

        viewModel.mSimilar.observe(viewLifecycleOwner, similarAdapter::setDataSet)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.loadSimilar()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun addInfo(inflater: LayoutInflater) = with(binding) {
        textViewName.text = details.name
        textViewMtype.text = details.type

        if (details.eng_name != null) {
            textViewEnName.visibility = View.VISIBLE
            textViewEnName.text = details.eng_name
        }

        textViewYear.text = getString(R.string.year, details.issue_year)
        imageView.visibility = View.VISIBLE
        textViewViews.text = details.total_views.toString().prettyCount()

        layoutInfo.addView(getInfoView("???????????? ????????????", 0, resources.getColor(R.color.gray)))
        layoutInfoAnswers.addView(
            getInfoView(
                details.status,
                0,
                mContext.themeColor(R.attr.tc)
            )
        )
        layoutInfo.addView(getInfoView("?????????????????? ????????", 1, resources.getColor(R.color.gray)))
        layoutInfoAnswers.addView(
            getInfoView(
                if (details.count_chapters > 0) details.count_chapters.toString()
                else "?????? ????????", 1, mContext.themeColor(R.attr.tc)
            )
        )

        layoutInfo.addView(getInfoView("??????????", 2, resources.getColor(R.color.gray)))
        layoutInfoAnswers.addView(
            getInfoView(
                details.author ?: "????????????????????",
                2,
                mContext.themeColor(R.attr.tc)
            )
        )

        textViewDesc.text = details.description

        details.tag.forEach { tag ->
            val chip = inflater.inflate(
                R.layout.item_chip_genre,
                chipGroup,
                false
            ) as Chip?
            chip?.chipEndPadding = 0f
            chip?.chipStartPadding = 0f
            chip?.chipCornerRadius = 16f
            chip?.text = tag.name
            chip?.isCheckable = false
            chipGroup.addView(chip)
        }
    }

    private fun getInfoView(title: String, id: Int, colorId: Int): TextView {
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 0, 12)
        val textView = TextView(mContext)
        textView.layoutParams = params
        textView.text = title
        textView.textSize = 14F
        textView.id = id
        textView.setPadding(4)
        textView.setTextColor(colorId)
        return textView
    }

}