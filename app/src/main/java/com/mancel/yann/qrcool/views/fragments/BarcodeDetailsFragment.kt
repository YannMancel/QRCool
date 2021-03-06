package com.mancel.yann.qrcool.views.fragments

import android.content.Intent
import android.net.Uri
import android.view.View
import android.webkit.URLUtil
import com.mancel.yann.qrcool.R
import com.mancel.yann.qrcool.models.*
import com.mancel.yann.qrcool.utils.BarcodeTools
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

    private fun updateUI() {
        // There is only one item that is not null
        val barcode = this._barcodes.find { it != null }

        // UI
        this.updateImage(barcode!!._format)
        this.updateData(barcode)
        this.updateDate(barcode._date)
        this.updateButton(barcode)
    }

    private fun updateImage(format: BarcodeOverlay.BarcodeFormat) {
        val resource = when (format) {
            BarcodeOverlay.BarcodeFormat.FORMAT_BARCODE_1D -> R.drawable.ic_barcode
            BarcodeOverlay.BarcodeFormat.FORMAT_BARCODE_2D -> R.drawable.ic_qrcode
            else -> R.drawable.ic_unknown
        }
        this._rootView.fragment_details_image.setImageResource(resource)
    }

    private fun updateData(barcode: BarcodeOverlay) {
        this._rootView.fragment_details_data.text = buildString {
            BarcodeTools.getStructuredDataOfBarcode(
                this@BarcodeDetailsFragment.requireContext(),
                barcode
            ).forEach { data ->
                if (!data.isNullOrEmpty()) append("$data\n")
            }
        }
    }

    private fun updateDate(date: Date) {
        val formattedDate =
            SimpleDateFormat("dd-MM-yyyy hh:mm", Locale.getDefault())
                .format(date)
        this._rootView.fragment_details_date.text = formattedDate
    }

    private fun updateButton(barcode: BarcodeOverlay) {
        val validUrlOrNull = when (barcode) {
            is UrlBarcode -> { this.checkValidityOfUrl(barcode._url) }
            else -> { this.checkValidityOfUrl(barcode._rawValue) }
        }

        // Visibility according to the valid url
        val visibility = if (!validUrlOrNull.isNullOrEmpty()) View.VISIBLE else View.GONE
        this._rootView.fragment_details_button.visibility = visibility

        // Listener of Button (so rawValue is not null)
        if (visibility == View.VISIBLE) {
            this._rootView.fragment_details_button.setOnClickListener {
                this.openLinkFromUrl(validUrlOrNull!!)
            }
        }
    }

    // -- URL --

    /**
     * Checks the validity of URL thanks to the URL in argument
     * @param url a [String] that contains the URL
     * @return a [String] if is a valid url else null
     */
    private fun checkValidityOfUrl(url: String?): String? {
        // No data
        if (url.isNullOrEmpty()) return null

        return when {
            URLUtil.isValidUrl(url) -> url
            url.startsWith("www") -> "https://${url}"
            else -> null
        }
    }

    private fun openLinkFromUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        this.startActivity(intent)
    }
}