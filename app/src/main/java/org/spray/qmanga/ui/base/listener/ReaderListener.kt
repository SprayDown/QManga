package org.spray.qmanga.ui.base.listener

interface ReaderListener {

    fun onPageChanged(currentPage: Int)
    fun forceShowBar()
    fun onRead()
}