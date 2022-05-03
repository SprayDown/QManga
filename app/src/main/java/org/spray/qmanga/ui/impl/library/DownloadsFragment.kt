package org.spray.qmanga.ui.impl.library

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.maxkeppeler.sheets.info.InfoSheet
import org.spray.qmanga.client.models.MangaData
import org.spray.qmanga.databinding.FragmentDownloadsPageBinding
import org.spray.qmanga.sqlite.QueryResponse
import org.spray.qmanga.sqlite.WRITE_TIME
import org.spray.qmanga.client.models.local.LocalManga
import org.spray.qmanga.sqlite.query.LocalMangaQuery
import org.spray.qmanga.sqlite.query.RecentQuery
import org.spray.qmanga.ui.base.listener.OnItemClickListener
import org.spray.qmanga.ui.impl.mangalist.MangaCardAdapter
import org.spray.qmanga.utils.autoFitColumns

class DownloadsFragment : Fragment() {

    private lateinit var binding: FragmentDownloadsPageBinding

    private var adapter: MangaCardAdapter? = null

    private val query = LocalMangaQuery()

    override fun onResume() {
        super.onResume()
        update()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDownloadsPageBinding.inflate(inflater, container, false)
        adapter = MangaCardAdapter(
            ArrayList(),
            requireActivity(),
            true,
            object : OnItemClickListener<MangaData> {
                override fun onLongClick(item: MangaData) {
                    InfoSheet().show(requireActivity()) {
                        content("Вы действительно хотите удалить мангу?")
                        onNegative("Отмена") {
                            update()
                        }
                        onPositive("Да") {
                            val query = RecentQuery()

                            query.delete(item.hashId, null)
                            update()
                        }
                    }
                }

                override fun onItemClick(item: MangaData) {
                }

            })

        update()

        binding.apply {
            recyclerView.autoFitColumns(122)
            recyclerView.adapter = adapter

            swipeRefreshLayout.setOnRefreshListener {
                update()
                swipeRefreshLayout.isRefreshing = false
            }
        }

        return binding.root
    }

    private fun update() {
        query.readAll("$WRITE_TIME DESC", object : QueryResponse<List<LocalManga>> {
            override fun onSuccess(data: List<LocalManga>) {
                adapter?.setDataSet(data)
            }

            override fun onFailure(msg: String) {
                Log.i("qmanga", msg)
            }
        })
    }

}