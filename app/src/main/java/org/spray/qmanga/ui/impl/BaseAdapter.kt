package org.spray.qmanga.ui.impl

import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<T, VH : RecyclerView.ViewHolder>(private var dataSet: List<T>,
                                                            val fragmentActivity: FragmentActivity?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return createVH(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        bind(holder as VH, dataSet[position], position)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    open fun setDataSet(data: List<T>) {
        dataSet = data
    }

    abstract fun createVH(parent: ViewGroup, viewType: Int) : VH
    abstract fun bind(holder: VH, data: T, position: Int)

}