package com.mancel.yann.qrcool.views.fragments

import android.content.Intent
import android.net.Uri
import android.view.View
import android.webkit.URLUtil
import com.mancel.yann.qrcool.R
import kotlinx.android.synthetic.main.fragment_details_q_r_code.view.*

/**
 * Created by Yann MANCEL on 06/08/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool.views.fragments
 *
 * A [BaseFragment] subclass.
 */
class DetailsQRCodeFragment : BaseFragment() {

    // FIELDS --------------------------------------------------------------------------------------

    private val _qRCode by lazy {
        DetailsQRCodeFragmentArgs.fromBundle(this.requireArguments()).qrcode
    }

    // METHODS -------------------------------------------------------------------------------------

    // -- BaseFragment --

    override fun getFragmentLayout(): Int = R.layout.fragment_details_q_r_code

    override fun configureDesign() = this.updateUI()

    // -- UI --

    /**
     * Updates the UI
     */
    private fun updateUI() {
        val rawValue = this._qRCode._text

        // Raw value
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