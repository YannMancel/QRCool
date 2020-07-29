package com.mancel.yann.qrcool.views.fragments

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.zxing.Result
import com.mancel.yann.qrcool.BuildConfig
import com.mancel.yann.qrcool.R
import com.mancel.yann.qrcool.viewModels.SharedViewModel
import kotlinx.android.synthetic.main.fragment_zxing.view.*
import me.dm7.barcodescanner.zxing.ZXingScannerView

/**
 * Created by Yann MANCEL on 21/07/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool.views.fragments
 *
 * A [BaseFragment] subclass which implements [ZXingScannerView.ResultHandler].
 */
class ZXingFragment : BaseFragment(), ZXingScannerView.ResultHandler {

    // FIELDS --------------------------------------------------------------------------------------

    private val _viewModel: SharedViewModel by activityViewModels()

    // METHODS -------------------------------------------------------------------------------------

    // -- BaseFragment --

    override fun getFragmentLayout(): Int = R.layout.fragment_zxing

    override fun configureDesign() {
        if (BuildConfig.QRCODE_SIMULATOR_ENABLED) {
            this.notifyScanOfQRCode("http://qrcode.scan")
        }
    }

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
        rawResult?.let {
            this.notifyScanOfQRCode(it.text)
        }
    }

    // -- Camera --

    /**
     * Starts the camera. It is the back camera by default
     */
    private fun startCamera() {
        if (this.hasCameraPermission()) {
            with(this._rootView.fragment_scan_view) {
                setResultHandler(this@ZXingFragment)
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

    // -- QR Code --

    /**
     * Notifies when a QR Code has been checked
     */
    private fun notifyScanOfQRCode(textOfQRCode: String) {
        // Add QR Code
        this._viewModel.addQRCode(textOfQRCode)

        // Finishes this fragment
        this.findNavController().popBackStack()
    }
}