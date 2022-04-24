package org.spray.qmanga.ui.base

import android.os.Bundle

abstract class ReturnableFragment(override val titleId: Int? = null) : BaseFragment(titleId) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

}