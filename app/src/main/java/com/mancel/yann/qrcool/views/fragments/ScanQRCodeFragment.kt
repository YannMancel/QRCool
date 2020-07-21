package com.mancel.yann.qrcool.views.fragments

import com.mancel.yann.qrcool.R

/**
 * Created by Yann MANCEL on 21/07/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool.views.fragments
 *
 * A [BaseFragment] subclass.
 */
class ScanQRCodeFragment : BaseFragment() {

    // METHODS -------------------------------------------------------------------------------------

    // -- BaseFragment --

    override fun getFragmentLayout(): Int = R.layout.fragment_scan_q_r_code

    override fun configureDesign() {}

    override fun actionAfterPermission() {
        // Action after granted permission
    }

    // -- Fragment --

    override fun onResume() {
        super.onResume()


    }
}