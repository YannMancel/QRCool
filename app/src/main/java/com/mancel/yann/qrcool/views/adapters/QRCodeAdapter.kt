package com.mancel.yann.qrcool.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mancel.yann.qrcool.R
import com.mancel.yann.qrcool.models.QRCode
import kotlinx.android.synthetic.main.item_q_r_code.view.*

/**
 * Created by Yann MANCEL on 22/07/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool.views.adapters
 *
 * A [RecyclerView.Adapter] subclass.
 */
class QRCodeAdapter : RecyclerView.Adapter<QRCodeAdapter.QRCodeViewHolder>() {

    // FIELDS --------------------------------------------------------------------------------------

    private val _qRCodes = mutableListOf<QRCode>()

    // METHODS -------------------------------------------------------------------------------------

    // -- RecyclerView.Adapter --

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QRCodeViewHolder {
        // Creates the View thanks to the inflater
        val view =
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_q_r_code, parent, false)

        return QRCodeViewHolder(view)
    }

    override fun onBindViewHolder(holder: QRCodeViewHolder, position: Int) {
        holder.bind(this._qRCodes[position])
    }

    override fun getItemCount(): Int = this._qRCodes.size

    // -- QRCode --

    /**
     * Updates data of [QRCodeAdapter]
     * @param newData a [List] of [QRCode]
     */
    fun updateData(newData: List<QRCode>) {
        // Optimizes the performances of RecyclerView
        val diffCallback  = QRCodeDiffCallback(this._qRCodes, newData)
        val diffResult  = DiffUtil.calculateDiff(diffCallback )

        // New data
        with(this._qRCodes) {
            clear()
            addAll(newData)
        }

        // Notifies adapter
        diffResult.dispatchUpdatesTo(this@QRCodeAdapter)
    }

    // NESTED CLASSES ------------------------------------------------------------------------------

    /**
     * A [RecyclerView.ViewHolder] subclass.
     */
    class QRCodeViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        // METHODS ---------------------------------------------------------------------------------

        /**
         * Binds the [QRCodeAdapter] and the [QRCodeViewHolder]
         */
        fun bind(qRCode: QRCode) {
            // Name
            this.itemView.item_name.text = qRCode._text
        }
    }
}