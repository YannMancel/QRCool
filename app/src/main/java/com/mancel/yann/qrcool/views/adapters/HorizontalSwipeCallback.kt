package com.mancel.yann.qrcool.views.adapters

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.mancel.yann.qrcool.R
import kotlin.math.abs

/**
 * Created by Yann MANCEL on 26/08/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool.views.adapters
 *
 * An [ItemTouchHelper.SimpleCallback] subclass.
 */
class HorizontalSwipeCallback(
    context: Context,
    val actionToSwipe: (adapterPosition: Int) -> Unit
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START or ItemTouchHelper.END) {

    // FIELDS --------------------------------------------------------------------------------------

    private val _background = ContextCompat.getDrawable(context, R.drawable.background_swipe)
    private val _backgroundCornerOffset = context.resources.getDimension(R.dimen.radius_card).toInt()
    private val _icon = ContextCompat.getDrawable(context, R.drawable.ic_delete)

    // METHODS -------------------------------------------------------------------------------------

    // -- ItemTouchHelper.SimpleCallback --

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        this.actionToSwipe(viewHolder.adapterPosition)
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
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        // First: Draw the background
        this.drawBackground(
            c,
            viewHolder,
            dX,
            this._background,
            this._backgroundCornerOffset
        )

        // Second: Draw the icon
        this.drawIcon(
            c,
            viewHolder,
            dX,
            this._icon
        )
    }

    // -- Canvas --

    private fun drawBackground(
        c: Canvas,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        background: Drawable?,
        backgroundCornerOffset: Int
    ) {
        // No Swipe or no Drawable
        if (dX == 0F || background == null) return

        val itemView = viewHolder.itemView

        when {
            /*
                Swipe to left: <------

                    +----------------+------+
                    |                |      |
                    |     item       | here |
                    |                |      |
                    +----------------+------+
             */
            dX < 0F -> {
                val backgroundLeft =
                    if (abs(dX) < itemView.width)
                        itemView.right + dX.toInt() - backgroundCornerOffset
                    else
                        itemView.right - itemView.width

                background.bounds = Rect(
                    backgroundLeft,
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
            }

            /*
                Swipe to Right: ------>

                    +------+----------------+
                    |      |                |
                    | here |     item       |
                    |      |                |
                    +------+----------------+
             */
            dX > 0F -> {
                val backgroundRight =
                    if (dX < itemView.width)
                        itemView.left + dX.toInt() + backgroundCornerOffset
                    else
                        itemView.left + itemView.width

                background.bounds = Rect(
                    itemView.left,
                    itemView.top,
                    backgroundRight,
                    itemView.bottom
                )
            }
        }

        background.draw(c)
    }

    private fun drawIcon(
        c: Canvas,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        icon: Drawable?
    ) {
        // No Swipe or no Drawable
        if (dX == 0F || icon == null) return

        val itemView = viewHolder.itemView
        val iconMargin = (itemView.height - icon.intrinsicHeight) / 2
        val iconTop = itemView.top + iconMargin
        val iconBottom = itemView.bottom - iconMargin

        when {
            /*
                Swipe to left: <------

                    +----------------+------+
                    |                |      |
                    |     item       | here |
                    |                |      |
                    +----------------+------+
             */
            dX < 0F -> {
                icon.bounds = Rect(
                    itemView.right - iconMargin - icon.intrinsicWidth,
                    iconTop,
                    itemView.right - iconMargin,
                    iconBottom
                )
            }

            /*
                Swipe to Right: ------>

                    +------+----------------+
                    |      |                |
                    | here |     item       |
                    |      |                |
                    +------+----------------+
             */
            dX > 0F -> {
                icon.bounds = Rect(
                    itemView.left + iconMargin,
                    iconTop,
                    itemView.left + iconMargin + icon.intrinsicWidth,
                    iconBottom
                )
            }
        }

        icon.draw(c)
    }
}