package org.spray.qmanga.ui.base.listener

interface OnItemClickListener<T> {

    fun onItemClick(item: T)
    fun onLongClick(item: T)

}