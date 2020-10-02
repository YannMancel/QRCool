package com.mancel.yann.qrcool.analyzers

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.mancel.yann.qrcool.models.*
import com.mancel.yann.qrcool.states.ScanState
import com.mancel.yann.qrcool.views.fragments.CameraXFragment

/**
 * Created by Yann MANCEL on 02/08/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool.analyzers
 *
 * A class which implements [ImageAnalysis.Analyzer].
 */
@androidx.camera.core.ExperimentalGetImage
class MLKitBarcodeAnalyzer(
    private val _config: CameraXFragment.ScanConfig,
    private val _actionOnScanResult: (ScanState) -> Unit
) : ImageAnalysis.Analyzer {

    /*
        See ML Kit:
            [1]: https://developers.google.com/ml-kit/vision/barcode-scanning/android
     */

    // METHODS -------------------------------------------------------------------------------------

    // -- ImageAnalysis.Analyzer interface --

    override fun analyze(image: ImageProxy) {
        // Warning: Do not forget to close the ImageProxy
        this.analyseImage(image)
    }

    // -- ML Kit - Barcode scanner --

    /** Analyses an [ImageProxy] in parameter with the [ImageAnalysis] use case of CameraX */
    private fun analyseImage(imageProxy: ImageProxy) {
        // Creates an InputImage object from a media.Image object
        imageProxy.image?.let {
            // InputImage
            val image = InputImage.fromMediaImage(it, imageProxy.imageInfo.rotationDegrees)

            // Pass image to an ML Kit Vision API
            this.scanBarcodes(image) {
                imageProxy.close()
            }
        } ?: imageProxy.close()
    }

    private fun scanBarcodes(image: InputImage, actionOnComplete: () -> Unit) {
        // Options
        val options = when (this._config) {
            CameraXFragment.ScanConfig.BARCODE_1D -> this.configureOptions1D()
            CameraXFragment.ScanConfig.BARCODE_2D -> this.configureOptions2D()
        }

        // Scanner
        val scanner = BarcodeScanning.getClient(options)

        // Result
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                val data = this.convertBarcodesFromMLKitToApp(barcodes)
                this._actionOnScanResult(
                    ScanState.Success(data)
                )
            }
            .addOnFailureListener { exception ->
                this._actionOnScanResult(
                    ScanState.Failure(exception)
                )
            }
            .addOnCompleteListener {
                actionOnComplete()
            }
    }

    // -- BarcodeScannerOptions --

    /**
     * Configures the [BarcodeScannerOptions] for barcode 1D.
     * Here, we know which barcode formats you expect
     * to read. This is improve the speed of the barcode detector by configuring it to only
     * detect those formats.
     * @return a [BarcodeScannerOptions]
     */
    private fun configureOptions1D(): BarcodeScannerOptions {
        return BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_CODABAR,
                Barcode.FORMAT_CODE_39,
                Barcode.FORMAT_CODE_93,
                Barcode.FORMAT_CODE_128,
                Barcode.FORMAT_EAN_8,
                Barcode.FORMAT_EAN_13,
                Barcode.FORMAT_UPC_A,
                Barcode.FORMAT_UPC_E,
                Barcode.FORMAT_ITF
            )
            .build()
    }

    /**
     * Configures the [BarcodeScannerOptions] for barcode 2D.
     * Here, we know which barcode formats you expect
     * to read. This is improve the speed of the barcode detector by configuring it to only
     * detect those formats.
     * @return a [BarcodeScannerOptions]
     */
    private fun configureOptions2D(): BarcodeScannerOptions {
        return BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_AZTEC,
                Barcode.FORMAT_PDF417,
                Barcode.FORMAT_DATA_MATRIX
            )
            .build()
    }

    // -- Barcode conversion --

    /** Converts the barcodes from ML Kit library to this application (abstract layer) */
    private fun convertBarcodesFromMLKitToApp(
        barcodesFromMLKit: List<Barcode>
    ) : List<BarcodeOverlay> {
        // No Data
        if (barcodesFromMLKit.isEmpty()) return emptyList()

        // It is possible to scan several barcodes on the same picture
        return mutableListOf<BarcodeOverlay>().also {
            barcodesFromMLKit.forEach { barcode ->
                val newBarcode: BarcodeOverlay = when (barcode.valueType) {
                    Barcode.TYPE_WIFI -> {
                        WifiBarcode(
                            _rawValue = barcode.rawValue,
                            _type = this.getType(barcode.valueType),
                            _format = this.getFormat(barcode.format),
                            _sSID = barcode.wifi?.ssid,
                            _password =barcode.wifi?.password,
                            _encryptionType = this.getWifiType(barcode.wifi!!.encryptionType)
                        )
                    }

                    Barcode.TYPE_URL -> {
                        UrlBarcode(
                            _rawValue = barcode.rawValue,
                            _type = this.getType(barcode.valueType),
                            _format = this.getFormat(barcode.format),
                            _title = barcode.url?.title,
                            _url = barcode.url?.url
                        )
                    }

                    Barcode.TYPE_SMS -> {
                        SMSBarcode(
                            _rawValue = barcode.rawValue,
                            _type = this.getType(barcode.valueType),
                            _format = this.getFormat(barcode.format),
                            _phoneNumber = barcode.sms?.phoneNumber,
                            _message = barcode.sms?.message
                        )
                    }

                    Barcode.TYPE_GEO -> {
                        GeoPointBarcode(
                            _rawValue = barcode.rawValue,
                            _type = this.getType(barcode.valueType),
                            _format = this.getFormat(barcode.format),
                            _latitude = barcode.geoPoint?.lat,
                            _longitude = barcode.geoPoint?.lng
                        )
                    }

                    else -> {
                        TextBarcode(
                            _rawValue = barcode.rawValue,
                            _type = this.getType(barcode.valueType),
                            _format = this.getFormat(barcode.format)
                        )
                    }
                }

                it.add(newBarcode)
            }
        }
    }

    // -- Type --

    /** Gets the barcode's type from ML Kit library to this application (abstract layer) */
    private fun getType(
        typeFromMLKit: Int
    ) : BarcodeOverlay.BarcodeType {
        return when (typeFromMLKit) {
            Barcode.TYPE_WIFI -> BarcodeOverlay.BarcodeType.TYPE_WIFI
            Barcode.TYPE_URL -> BarcodeOverlay.BarcodeType.TYPE_URL
            Barcode.TYPE_SMS -> BarcodeOverlay.BarcodeType.TYPE_SMS
            Barcode.TYPE_GEO -> BarcodeOverlay.BarcodeType.TYPE_GEO
            else -> BarcodeOverlay.BarcodeType.TYPE_TEXT
        }
    }

    // -- Format --

    /** Gets the barcode's format from ML Kit library to this application (abstract layer) */
    private fun getFormat(
        formatFromMLKit: Int
    ) : BarcodeOverlay.BarcodeFormat {
        return when (formatFromMLKit) {
            Barcode.FORMAT_CODABAR,
            Barcode.FORMAT_CODE_39,
            Barcode.FORMAT_CODE_93,
            Barcode.FORMAT_CODE_128,
            Barcode.FORMAT_EAN_8,
            Barcode.FORMAT_EAN_13,
            Barcode.FORMAT_UPC_A,
            Barcode.FORMAT_UPC_E,
            Barcode.FORMAT_ITF -> BarcodeOverlay.BarcodeFormat.FORMAT_BARCODE_1D

            Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_AZTEC,
            Barcode.FORMAT_PDF417,
            Barcode.FORMAT_DATA_MATRIX -> BarcodeOverlay.BarcodeFormat.FORMAT_BARCODE_2D

            else -> BarcodeOverlay.BarcodeFormat.FORMAT_UNKNOWN
        }
    }

    // -- Wifi type --

    /** Gets the barcode's wifi type from ML Kit library to this application (abstract layer) */
    private fun getWifiType(
        wifiTypeFromMLKit: Int
    ) : BarcodeOverlay.WifiType {
        return when (wifiTypeFromMLKit) {
            Barcode.WiFi.TYPE_WEP -> BarcodeOverlay.WifiType.TYPE_WEP
            Barcode.WiFi.TYPE_WPA -> BarcodeOverlay.WifiType.TYPE_WPA
            else -> BarcodeOverlay.WifiType.TYPE_OPEN
        }
    }
}