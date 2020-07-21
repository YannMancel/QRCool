package com.mancel.yann.qrcool.views.fragments

import android.util.Log
import com.google.zxing.Result
import com.mancel.yann.qrcool.R
import kotlinx.android.synthetic.main.fragment_scan_q_r_code.view.*
import me.dm7.barcodescanner.zxing.ZXingScannerView

/**
 * Created by Yann MANCEL on 21/07/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool.views.fragments
 *
 * A [BaseFragment] subclass which implements [ZXingScannerView.ResultHandler].
 */
class ScanQRCodeFragment : BaseFragment(), ZXingScannerView.ResultHandler {

    // METHODS -------------------------------------------------------------------------------------

    // -- BaseFragment --

    override fun getFragmentLayout(): Int = R.layout.fragment_scan_q_r_code

    override fun configureDesign() { /* Do nothing here */ }

    override fun actionAfterPermission() = this.startCamera()

    // -- Fragment --

    override fun onResume() {
        super.onResume()
        this.startCamera()
    }

    override fun onPause() {
        super.onPause()
        this.stopCamera()
    }

    // -- ZXingScannerView.ResultHandler interface --

    override fun handleResult(rawResult: Result?) {
        Log.d("Scan", "QR Code: ${rawResult?.text}")
        // todo - 21/07/2020 - Create a popup
    }

    // -- Camera --

    /**
     * Starts the camera. It is the back camera by default
     */
    private fun startCamera() {
        if (this.hasCameraPermission()) {
            with(this._rootView.fragment_scan_view) {
                setResultHandler(this@ScanQRCodeFragment)
                startCamera()
            }
        }
    }

    /**
     * Stop the camera to avoid the memory leaks
     */
    private fun stopCamera() {
        this._rootView.fragment_scan_view.stopCamera()
    }
}