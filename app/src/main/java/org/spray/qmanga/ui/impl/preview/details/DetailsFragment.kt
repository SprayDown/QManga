package org.spray.qmanga.ui.impl.preview.details

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip
import org.spray.qmanga.R
import org.spray.qmanga.client.models.MangaDetails
import org.spray.qmanga.databinding.FragmentDetailsBinding
import org.spray.qmanga.ui.impl.preview.PreviewViewModel
import org.spray.qmanga.utils.ext.prettyCount
import org.spray.qmanga.utils.ext.themeColor


class DetailsFragment(private val details: MangaDetails) : Fragment() {

    private lateinit var binding: FragmentDetailsBinding
    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mContext = requireContext()
        binding = FragmentDetailsBinding.inflate(inflater, container, false)

        binding.apply {
            textViewName.text = details.name
            textViewMtype.text = details.type

            if (details.eng_name != null) {
                textViewEnName.visibility = View.VISIBLE
                textViewEnName.text = details.eng_name
            }

            textViewYear.text = getString(R.string.year, details.issue_year)
            imageView.visibility = View.VISIBLE
            textViewViews.text = details.total_views.toString().prettyCount()

            layoutInfo.addView(getInfoView("Статус тайтла", 0, resources.getColor(R.color.gray)))
            layoutInfoAnswers.addView(
                getInfoView(
                    details.status,
                    0,
                    mContext.themeColor(R.attr.tc)
                )
            )
            layoutInfo.addView(getInfoView("Загружено глав", 1, resources.getColor(R.color.gray)))
            layoutInfoAnswers.addView(
                getInfoView(
                    if (details.count_chapters > 0) details.count_chapters.toString()
                    else "Нет глав", 1, mContext.themeColor(R.attr.tc)
                )
            )

            layoutInfo.addView(getInfoView("Автор", 2, resources.getColor(R.color.gray)))
            layoutInfoAnswers.addView(
                getInfoView(
                    details.author ?: "Неизвестно",
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

        return binding.root
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
        textView.textSize = 15F
        textView.id = id
        textView.setPadding(4)
        textView.setTextColor(colorId)
        return textView
    }

}