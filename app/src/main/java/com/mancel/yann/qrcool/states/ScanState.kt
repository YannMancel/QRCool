package com.mancel.yann.qrcool.states

import com.mancel.yann.qrcool.models.BarcodeOverlay

/**
 * Created by Yann MANCEL on 15/08/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool.states
 */
sealed class ScanState {

    // CLASSES -------------------------------------------------------------------------------------

    /**
     * State:  SuccessScan
     * Where:  MLKitBarcodeAnalyzer#scanBarcodes
     * Why:    Scan is a success
     */
    class SuccessScan(val _barcodes: List<BarcodeOverlay>) : ScanState()

    /**
     * State:  FailedScan
     * Where:  MLKitBarcodeAnalyzer#scanBarcodes
     * Why:    Scan is a fail
     */
    class FailedScan(val _exception: Exception) : ScanState()
}