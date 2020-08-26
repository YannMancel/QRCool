package com.mancel.yann.qrcool.views.adapters

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Yann MANCEL on 26/08/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool.views.adapters
 *
 * An [ItemTouchHelper.SimpleCallback] subclass.
 */
class HorizontalSwipeCallback(
    val actionToSwipe: (adapterPosition: Int) -> Unit
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START or ItemTouchHelper.END) {

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
}