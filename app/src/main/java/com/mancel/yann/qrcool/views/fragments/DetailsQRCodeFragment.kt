package com.mancel.yann.qrcool.views.fragments

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
        // Raw value
        this._rootView.fragment_details_raw_value.text = this._qRCode._text
    }
}