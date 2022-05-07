package org.spray.qmanga.ui.impl.preview.chapters

import android.content.Context
import android.graphics.Canvas
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import org.spray.qmanga.R

open class ChapterTouchCallback(
    val context: Context,
    val adapter: PreviewChaptersAdapter?
) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val pos = viewHolder.absoluteAdapterPosition
        if (direction == ItemTouchHelper.LEFT) {
            val chapter = adapter?.getDataSet()?.get(pos)!!
            chapter.read = !chapter.read
        }
        swipeRefresh(pos)
    }

    open fun swipeRefresh(pos: Int) {}

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val pos = viewHolder.absoluteAdapterPosition
        val chapter = adapter?.getDataSet()?.get(pos)!!

        RecyclerViewSwipeDecorator.Builder(
            c, recyclerView, viewHolder, dX,
            dY, actionState, isCurrentlyActive
        )
            .addBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.green_touch
                )
            )
            .setIconHorizontalMargin(18)
            .addActionIcon(
                if (chapter.read) R.drawable.ic_baseline_visibility_24
                else R.drawable.ic_baseline_visibility_off_24
            )
            .create()
            .decorate();

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}