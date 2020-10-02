package com.mancel.yann.qrcool.states

import com.mancel.yann.qrcool.analyzers.MLKitBarcodeAnalyzer
import com.mancel.yann.qrcool.models.BarcodeOverlay

/**
 * Created by Yann MANCEL on 15/08/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool.states
 */
sealed class ScanState {

    // CLASSES -------------------------------------------------------------------------------------

    /**
     * State:  Success
     * Where:  [MLKitBarcodeAnalyzer.scanBarcodes]
     * Why:    Scan is a success
     */
    class Success(val _barcodes: List<BarcodeOverlay>) : ScanState()

    /**
     * State:  Failure
     * Where:  [MLKitBarcodeAnalyzer.scanBarcodes]
     * Why:    Scan is a failure
     */
    class Failure(val _exception: Exception) : ScanState()
}