package org.spray.qmanga.ui.impl.preview.chapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import org.spray.qmanga.client.models.MangaDetails
import org.spray.qmanga.databinding.FragmentChaptersBinding

class ChaptersFragment(val details: MangaDetails) : Fragment() {

    private lateinit var binding: FragmentChaptersBinding
    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mContext = container?.context!!
        binding = FragmentChaptersBinding.inflate(inflater, container, false)

        val adapter = details.chapters?.let { ChaptersAdapter(it, activity) }

        binding.apply {
            recyclerView.layoutManager = LinearLayoutManager(context);

            recyclerView.adapter = adapter
        }
        return binding.root
    }
}