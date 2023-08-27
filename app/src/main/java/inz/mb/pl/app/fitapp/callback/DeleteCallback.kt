package inz.mb.pl.app.fitapp.callback

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import inz.mb.pl.app.fitapp.R

abstract class DeleteCallback(context: Context) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    private val deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_delete)
    private val background = ContextCompat.getDrawable(context, R.drawable.component_red_round_drawable)

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val item = viewHolder.itemView

        if (dX == 0f && !isCurrentlyActive) {
            clearCanvas(c, item.right + dX, item.top.toFloat(), item.right.toFloat(), item.bottom.toFloat())
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }

        background?.setBounds(item.right + dX.toInt(), item.top, item.right, item.bottom)
        background?.draw(c)

        val intrinsicHeight = deleteIcon?.intrinsicHeight ?: 0
        val iconHeight = item.bottom - item.top
        val iconTop = item.top + (iconHeight - intrinsicHeight) / 2
        val iconMargin = (iconHeight - intrinsicHeight) / 2

        deleteIcon?.setBounds(item.right - iconMargin - deleteIcon.intrinsicWidth, iconTop, item.right - iconMargin, iconTop + intrinsicHeight)
        deleteIcon?.draw(c)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        c?.drawRect(left, top, right, bottom, Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) })
    }

}