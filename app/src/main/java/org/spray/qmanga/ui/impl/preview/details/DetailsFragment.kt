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
import org.spray.qmanga.utils.themeColor


class DetailsFragment(private val details: MangaDetails) : Fragment() {

    private lateinit var binding: FragmentDetailsBinding
    private lateinit var mContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mContext = container?.context!!
        binding = FragmentDetailsBinding.inflate(inflater, container, false)

        binding.apply {
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
                    details.chapters?.size?.toString()
                        ?: "Нет глав", 1, mContext.themeColor(R.attr.tc)
                )
            )

            layoutInfo.addView(getInfoView("Автор", 2, resources.getColor(R.color.gray)))
            layoutInfoAnswers.addView(
                getInfoView(
                    details.data.author ?: "Неизвестно",
                    2,
                    mContext.themeColor(R.attr.tc)
                )
            )

            descriptionText.text = details.description

            details.tag.forEach { tag ->
                val chip = inflater.inflate(
                    R.layout.item_chip_genre,
                    chipGroup,
                    false
                ) as Chip?
                chip?.text = tag.name
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
        params.setMargins(0, 0, 0, 14)
        val textView = TextView(mContext)
        textView.layoutParams = params
        textView.text = title
        textView.textSize = 15F
        textView.id = id
        textView.setPadding(6)
        textView.setTextColor(colorId)
        return textView
    }

}