package org.spray.qmanga.ui.impl.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.spray.qmanga.databinding.FragmentDownloadsPageBinding

class DownloadsFragment : Fragment() {

    private lateinit var binding: FragmentDownloadsPageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDownloadsPageBinding.inflate(inflater, container, false)

        return binding.root
    }

}