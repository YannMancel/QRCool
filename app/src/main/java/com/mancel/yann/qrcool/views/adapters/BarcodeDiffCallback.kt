package com.mancel.yann.qrcool.views.adapters

import androidx.recyclerview.widget.DiffUtil
import com.mancel.yann.qrcool.models.BarcodeOverlay

/**
 * Created by Yann MANCEL on 22/07/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool.views.adapters
 *
 * A [DiffUtil.Callback] subclass.
 */
class BarcodeDiffCallback(
    private val _oldList: List<BarcodeOverlay>,
    private val _newList: List<BarcodeOverlay>
) : DiffUtil.Callback() {

    // METHODS -------------------------------------------------------------------------------------

    // -- DiffUtil.Callback --

    override fun getOldListSize(): Int = this._oldList.size

    override fun getNewListSize(): Int = this._newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // Comparison on all fields
        return this._oldList[oldItemPosition] == this._newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // Comparison on all fields
        return this._oldList[oldItemPosition] == this._newList[newItemPosition]
    }
}