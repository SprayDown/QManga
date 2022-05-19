package org.spray.qmanga.ui.impl.profile

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setPadding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import org.spray.qmanga.R
import org.spray.qmanga.client.models.user.UserData
import org.spray.qmanga.databinding.FragmentUserInfoBinding
import org.spray.qmanga.utils.ext.themeColor


class UserInfoBottomSheet(private val userData: UserData) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentUserInfoBinding

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserInfoBinding.inflate(inflater, container, false)

        binding.cancelBtn.setOnClickListener { dismiss() }

        setInfo(inflater)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout = bottomSheetDialog.findViewById<View>(
                com.google.android.material.R.id.design_bottom_sheet
            )
            parentLayout?.let { bottomSheet ->
                bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
                bottomSheetBehavior.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO
                binding.extraSpace.minimumHeight =
                    (Resources.getSystem().displayMetrics.heightPixels) / 2

                bottomSheetBehavior.addBottomSheetCallback(object :
                    BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (BottomSheetBehavior.STATE_EXPANDED == newState) {
                            binding.cancelBtn.visibility = View.VISIBLE
                        }
                        if (BottomSheetBehavior.STATE_COLLAPSED == newState) {
                            binding.cancelBtn.visibility = View.GONE
                        }
                        if (BottomSheetBehavior.STATE_HIDDEN == newState) {
                            dismiss()
                        }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {

                    }
                })
            }
        }

        return dialog
    }

    override fun onStart() {
        super.onStart()

        if (this::bottomSheetBehavior.isInitialized)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun setInfo(inflater: LayoutInflater) = with(binding) {
        textViewChaptersRead.text = userData.count_views.toString()
        textViewChaptersLiked.text = userData.count_votes.toString()

        textViewBadges.visibility = if (userData.badges.isNotEmpty()) View.VISIBLE else View.GONE

        userData.badges.forEach { badge ->
            val chip = inflater.inflate(
                R.layout.item_chip_badge,
                chipGroup,
                false
            ) as Chip?
            chip?.chipEndPadding = 4f
            chip?.chipStartPadding = 4f
            chip?.chipCornerRadius = 12f
            chip?.text = badge
            chip?.isCheckable = false
            chipGroup.addView(chip)
        }

        if (userData.sex != 0) {
            layoutInfo.addView(getInfoView("Пол", 1, resources.getColor(R.color.gray)))
            val sexTitle = if (userData.sex == 2) "Женский" else "Мужской"
            layoutInfoAnswers.addView(
                getInfoView(
                    sexTitle,
                    1,
                    requireContext().themeColor(R.attr.tc)
                )
            )
        }
    }

    private fun getInfoView(title: String, id: Int, colorId: Int): TextView {
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 0, 12)
        val textView = TextView(requireContext())
        textView.layoutParams = params
        textView.text = title
        textView.textSize = 14F
        textView.id = id
        textView.setPadding(4)
        textView.setTextColor(colorId)
        return textView
    }

    companion object {
        const val TAG = "UserInfoBottomSheet"
    }
}