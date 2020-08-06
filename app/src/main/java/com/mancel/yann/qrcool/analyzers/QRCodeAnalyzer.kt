package com.mancel.yann.qrcool.analyzers

import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

/**
 * Created by Yann MANCEL on 02/08/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool.analyzers
 *
 * A class which implements [ImageAnalysis.Analyzer].
 */
@androidx.camera.core.ExperimentalGetImage
class QRCodeAnalyzer(
    private val _actionOnSuccess: (List<Barcode>) -> Unit
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

    /**
     * Analyses an [ImageProxy] in parameter with the [ImageAnalysis] use case of CameraX
     * @param imageProxy an [ImageProxy]
     */
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

    /**
     * Scans the barcodes thanks to ML Kit
     * @param image an [InputImage]
     */
    private fun scanBarcodes(image: InputImage, actionOnComplete: () -> Unit) {
        // Options
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE
            )
            .build()

        // Scanner
        val scanner = BarcodeScanning.getClient(options)

        // Result
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                this.analyseBarcodes(barcodes)
            }
            .addOnFailureListener { e ->
                Log.e(
                    this.javaClass.simpleName,
                    "Barcode scanning failed: $e"
                )
            }
            .addOnCompleteListener {
                actionOnComplete()
            }
    }

    /**
     * Analyses the barcodes in argument
     * @param barcodes a [List] of [Barcode]
     */
    private fun analyseBarcodes(barcodes: List<Barcode>) = this._actionOnSuccess(barcodes)
}