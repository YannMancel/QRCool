package com.mancel.yann.qrcool.views.fragments

import android.content.Intent
import android.net.Uri
import android.view.View
import android.webkit.URLUtil
import com.mancel.yann.qrcool.R
import com.mancel.yann.qrcool.models.BarcodeOverlay
import kotlinx.android.synthetic.main.fragment_details_barcode.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Yann MANCEL on 06/08/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool.views.fragments
 *
 * A [BaseFragment] subclass.
 */
class BarcodeDetailsFragment : BaseFragment() {

    // FIELDS --------------------------------------------------------------------------------------

    private val _barcodes by lazy {
        mutableListOf<BarcodeOverlay?>().also {
            it.add(BarcodeDetailsFragmentArgs.fromBundle(this.requireArguments()).textBarcode)
            it.add(BarcodeDetailsFragmentArgs.fromBundle(this.requireArguments()).wifiBarcode)
            it.add(BarcodeDetailsFragmentArgs.fromBundle(this.requireArguments()).urlBarcode)
            it.add(BarcodeDetailsFragmentArgs.fromBundle(this.requireArguments()).smsBarcode)
            it.add(BarcodeDetailsFragmentArgs.fromBundle(this.requireArguments()).geoBarcode)
        }
    }

    // METHODS -------------------------------------------------------------------------------------

    // -- BaseFragment --

    override fun getFragmentLayout(): Int = R.layout.fragment_details_barcode

    override fun doOnCreateView() = this.updateUI()

    // -- UI --

    /**
     * Updates the UI
     */
    private fun updateUI() {
        // There is only one item that is not null
        val barcode = this._barcodes.find { it != null }

        // UI
        this.updateImage(barcode!!._format)
        this.updateRawValue(barcode._rawValue)
        this.updateDate(barcode._date)
        this.updateButton(barcode._rawValue)
    }

    /**
     * Updates the image
     */
    private fun updateImage(format: BarcodeOverlay.BarcodeFormat) {
        val resource = when(format) {
            BarcodeOverlay.BarcodeFormat.FORMAT_BARCODE_1D -> R.drawable.ic_barcode
            BarcodeOverlay.BarcodeFormat.FORMAT_BARCODE_2D -> R.drawable.ic_qrcode
            else -> R.drawable.ic_unknown
        }
        this._rootView.fragment_details_image.setImageResource(resource)
    }

    /**
     * Updates the raw value
     */
    private fun updateRawValue(rawValue: String?) {
        this._rootView.fragment_details_raw_value.text = rawValue
            ?: this.getString(R.string.no_raw_value)
    }

    /**
     * Updates the date
     */
    private fun updateDate(date: Date) {
        val formattedDate =
            SimpleDateFormat("dd-MM-yyyy hh:mm", Locale.getDefault())
                .format(date)
        this._rootView.fragment_details_date.text = formattedDate
    }

    /**
     * Updates the button
     */
    private fun updateButton(rawValue: String?) {
        // Button according to if raw value is a valid url
        val visibility = if (URLUtil.isValidUrl(rawValue)) View.VISIBLE else View.GONE
        this._rootView.fragment_details_button.visibility = visibility

        // Listener of Button (so rawValue is not null)
        if (visibility == View.VISIBLE) {
            this._rootView.fragment_details_button.setOnClickListener {
                this.openLinkFromUrl(rawValue!!)
            }
        }
    }

    // -- URL --

    /**
     * Opens a link thanks to the URL in argument
     * @param url a [String] that contains the URL
     */
    private fun openLinkFromUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }

        this.startActivity(intent)
    }
}