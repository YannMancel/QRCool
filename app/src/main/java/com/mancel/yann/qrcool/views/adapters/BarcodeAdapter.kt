package com.mancel.yann.qrcool.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mancel.yann.qrcool.R
import com.mancel.yann.qrcool.models.*
import com.mancel.yann.qrcool.utils.BarcodeTools
import kotlinx.android.synthetic.main.item_barcode.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Yann MANCEL on 22/07/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool.views.adapters
 *
 * A [RecyclerView.Adapter] subclass.
 */
class BarcodeAdapter(
    private val _context: Context,
    private val _actionOnClick: (item: BarcodeOverlay) -> Unit
) : RecyclerView.Adapter<BarcodeAdapter.QRCodeViewHolder>() {

    // FIELDS --------------------------------------------------------------------------------------

    private val _barcodes = mutableListOf<BarcodeOverlay>()

    // METHODS -------------------------------------------------------------------------------------

    // -- RecyclerView.Adapter --

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QRCodeViewHolder {
        // Creates the View thanks to the inflater
        val view =
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_barcode, parent, false)

        return QRCodeViewHolder(view)
    }

    override fun onBindViewHolder(holder: QRCodeViewHolder, position: Int) {
        holder.bind(
            this._barcodes[position],
            this._context,
            this._actionOnClick
        )
    }

    override fun getItemCount(): Int = this._barcodes.size

    // -- Barcode --

    /**
     * Updates data of [BarcodeAdapter]
     * @param newData a [List] of [BarcodeOverlay]
     */
    fun updateData(newData: List<BarcodeOverlay>) {
        // Optimizes the performances of RecyclerView
        val diffCallback  = BarcodeDiffCallback(this._barcodes, newData)
        val diffResult  = DiffUtil.calculateDiff(diffCallback )

        // New data
        with(this._barcodes) {
            clear()
            addAll(newData)
        }

        // Notifies adapter
        diffResult.dispatchUpdatesTo(this@BarcodeAdapter)
    }

    /**
     * Gets the [BarcodeOverlay] at the position in argument
     */
    fun getDataAt(position: Int): BarcodeOverlay = this._barcodes[position]

    // NESTED CLASSES ------------------------------------------------------------------------------

    /**
     * A [RecyclerView.ViewHolder] subclass.
     */
    class QRCodeViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        // METHODS ---------------------------------------------------------------------------------

        /**
         * Binds the [BarcodeAdapter] and the [QRCodeViewHolder]
         */
        fun bind(
            barcode: BarcodeOverlay,
            context: Context,
            actionOnClick: (BarcodeOverlay) -> Unit
        ) {
            // CardView
            this.itemView.item_card_view.setOnClickListener { actionOnClick(barcode) }

            // UI
            this.updateImage(barcode._format)
            this.updateData(barcode, context)
            this.updateDate(barcode._date)
        }

        /**
         * Updates the image
         */
        private fun updateImage(format: BarcodeOverlay.BarcodeFormat) {
            val resource = when (format) {
                BarcodeOverlay.BarcodeFormat.FORMAT_BARCODE_1D -> R.drawable.ic_barcode
                BarcodeOverlay.BarcodeFormat.FORMAT_BARCODE_2D -> R.drawable.ic_qrcode
                else -> R.drawable.ic_unknown
            }
            this.itemView.item_image.setImageResource(resource)
        }

        /**
         * Updates the data
         */
        private fun updateData(barcode: BarcodeOverlay, context: Context) {
            val data = BarcodeTools.getStructuredDataOfBarcode(context, barcode)

            this.updateTextView(this.itemView.item_parameter_A, data[0])
            this.updateTextView(this.itemView.item_parameter_B, data[1])
            this.updateTextView(this.itemView.item_parameter_C, data[2])
        }

        /**
         * Updates the [TextView]
         */
        private fun updateTextView(textView: TextView, data: String?) {
            if (data.isNullOrEmpty()) {
                textView.visibility = View.GONE
            } else {
                textView.text = data
            }
        }

        /**
         * Updates the [Date]
         */
        private fun updateDate(date: Date) {
            val formattedDate =
                SimpleDateFormat("dd-MM-yyyy hh:mm", Locale.getDefault())
                    .format(date)
            this.itemView.item_date.text = formattedDate
        }
    }
}