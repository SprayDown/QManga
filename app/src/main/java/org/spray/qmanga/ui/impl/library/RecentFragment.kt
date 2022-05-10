package org.spray.qmanga.ui.impl.library

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.maxkeppeler.sheets.info.InfoSheet
import org.spray.qmanga.client.models.MangaData
import org.spray.qmanga.databinding.FragmentRecentPageBinding
import org.spray.qmanga.sqlite.QueryResponse
import org.spray.qmanga.sqlite.WRITE_TIME
import org.spray.qmanga.client.models.MangaRecent
import org.spray.qmanga.sqlite.query.RecentQuery
import org.spray.qmanga.ui.base.listener.OnItemClickListener
import org.spray.qmanga.ui.fragments.ElementsUpdateListener
import org.spray.qmanga.ui.impl.mangalist.MangaCardAdapter
import org.spray.qmanga.utils.autoFitColumns

class RecentFragment : Fragment() {

    private lateinit var binding: FragmentRecentPageBinding

    private var adapter: MangaCardAdapter? = null

    private val query = RecentQuery()

    private var listener: ElementsUpdateListener? = null

    override fun onResume() {
        super.onResume()
        update()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecentPageBinding.inflate(inflater, container, false)
        adapter = MangaCardAdapter(
            ArrayList(),
            requireActivity(),
            true,
            object : OnItemClickListener<MangaData> {
                override fun onLongClick(data: MangaData) {
                    InfoSheet().show(requireActivity()) {
                        content("Вы действительно хотите удалить мангу?")
                        onNegative("Отмена") {
                            update()
                        }
                        onPositive("Да") {
                            val query = RecentQuery()

                            query.delete(data.hashId, null)
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

    fun setUpdateListener(listener: ElementsUpdateListener) {
        this.listener = listener
    }

    private fun update() {
        query.readAll("$WRITE_TIME DESC", object : QueryResponse<List<MangaRecent>> {
            override fun onSuccess(data: List<MangaRecent>) {
                adapter?.setDataSet(data)
                listener?.onRecentUpdate(data.size)
            }

            override fun onFailure(msg: String) {
                Log.i("qmanga", msg)
            }
        })
    }
}