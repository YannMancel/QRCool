package com.mancel.yann.qrcool.views.fragments

import android.content.Intent
import android.net.Uri
import android.view.View
import android.webkit.URLUtil
import com.mancel.yann.qrcool.R
import com.mancel.yann.qrcool.models.BarcodeOverlay
import kotlinx.android.synthetic.main.fragment_details_barcode.view.*

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
        // There is only one item that can be not null
        val barcode = this._barcodes.find { it != null }

        // Image
        val resource = when (barcode?._format) {
            BarcodeOverlay.BarcodeFormat.FORMAT_BARCODE_1D -> R.drawable.ic_barcode
            BarcodeOverlay.BarcodeFormat.FORMAT_BARCODE_2D -> R.drawable.ic_qrcode
            else -> R.drawable.ic_unknown
        }
        this._rootView.fragment_details_image.setImageResource(resource)

        // Raw value
        val rawValue = barcode?._rawValue
            ?: this.getString(R.string.no_raw_value)
        this._rootView.fragment_details_raw_value.text = rawValue

        // Button according to if raw value is a valid url
        val visibility = if (URLUtil.isValidUrl(rawValue)) {
            // Listener of Button
            this._rootView.fragment_details_button.setOnClickListener {
                this.openLinkFromUrl(rawValue)
            }

            View.VISIBLE
        } else {
            View.GONE
        }
        this._rootView.fragment_details_button.visibility = visibility
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