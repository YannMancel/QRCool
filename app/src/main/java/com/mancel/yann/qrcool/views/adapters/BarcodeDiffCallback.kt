package com.mancel.yann.qrcool.views.adapters

import androidx.recyclerview.widget.DiffUtil
import com.mancel.yann.qrcool.models.*

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
        // Comparison on their raw value
        return this._oldList[oldItemPosition]._rawValue == this._newList[newItemPosition]._rawValue
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // No same child class (parent class is a sealed class)
        if (this._oldList[oldItemPosition].javaClass.simpleName
            != this._newList[oldItemPosition].javaClass.simpleName
        ) return false

        // Comparison on all fields (data class)
        return when (this._oldList[oldItemPosition]) {
            is TextBarcode -> this._oldList[oldItemPosition] == this._newList[newItemPosition]
            is WifiBarcode -> this._oldList[oldItemPosition] == this._newList[newItemPosition]
            is UrlBarcode -> this._oldList[oldItemPosition] == this._newList[newItemPosition]
            is SMSBarcode -> this._oldList[oldItemPosition] == this._newList[newItemPosition]
            is GeoPointBarcode -> this._oldList[oldItemPosition] == this._newList[newItemPosition]
        }
    }
}