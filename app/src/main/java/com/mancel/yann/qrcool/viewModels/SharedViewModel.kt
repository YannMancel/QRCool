package com.mancel.yann.qrcool.viewModels

import androidx.camera.core.CameraSelector
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mancel.yann.qrcool.models.QRCode
import com.mancel.yann.qrcool.states.CameraState

/**
 * Created by Yann MANCEL on 22/07/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool.viewModels
 *
 * A [ViewModel] subclass.
 */
class SharedViewModel :  ViewModel() {

    // FIELDS --------------------------------------------------------------------------------------

    private val _barcodes = MutableLiveData<List<QRCode>>()

    private val _isFABMenuOpen = MutableLiveData<Boolean>()

    private val _cameraState = MutableLiveData<CameraState>()
    private var _lensFacing: Int = CameraSelector.LENS_FACING_BACK

    // CONSTRUCTORS --------------------------------------------------------------------------------

    init {
        // By default, the FAB menu is closed
        this._isFABMenuOpen.value = false
    }

    // METHODS -------------------------------------------------------------------------------------

    // -- Barcode --

    /**
     * Gets the [LiveData] of [List] of [QRCode]
     */
    fun getBarcodes(): LiveData<List<QRCode>> = this._barcodes

    /**
     * Adds barcodes to the [MutableLiveData] of [List] of [QRCode]
     * @param barcodes a [List] of [QRCode]
     */
    fun addBarcodes(barcodes: List<QRCode>) {
        val currentData =  this._barcodes.value?.toMutableList() ?: mutableListOf()

        barcodes.forEach { newBarcode ->
            // IMPOSSIBLE: 2 barcodes with the same raw value
            if (currentData.none { it._rawValue == newBarcode._rawValue }) {
                currentData.add(newBarcode)
            }
        }

        // Notify
        this._barcodes.value = currentData
    }

    /**
     * Adds a barcode to the [MutableLiveData] of [List] of [QRCode]
     * @param barcode a [QRCode]
     */
    fun addBarcode(barcode: QRCode) {
        val currentData =  this._barcodes.value?.toMutableList() ?: mutableListOf()

        // IMPOSSIBLE: 2 barcodes with the same raw value
        if (currentData.none { it._rawValue == barcode._rawValue }) {
            currentData.add(barcode)
        }

        // Notify
        this._barcodes.value = currentData
    }

    // -- FAB Menu --

    /**
     * Gets the [LiveData] of [Boolean]
     */
    fun isFABMenuOpen() : LiveData<Boolean> = this._isFABMenuOpen

    /**
     * Toogles the FloatingActionButton menu
     */
    fun toogleFabMenu() {
        this._isFABMenuOpen.value = !this._isFABMenuOpen.value!!
    }

    // -- CameraState --

    fun getCameraState(): LiveData<CameraState> = this._cameraState

    /**
     * Changes [CameraState] with [CameraState.SetupCamera] state
     */
    fun changeCameraStateToSetupCamera() {
        this._cameraState.value = CameraState.SetupCamera(
            this._lensFacing
        )
    }

    /**
     * Changes [CameraState] with [CameraState.PreviewReady] state
     */
    fun changeCameraStateToPreviewReady() {
        this._cameraState.value = CameraState.PreviewReady(
            this._lensFacing
        )
    }

    /**
     * Changes [CameraState] with [CameraState.Error] state
     */
    fun changeCameraStateToError(errorMessage: String) {
        this._cameraState.value = CameraState.Error(
            errorMessage,
            this._lensFacing
        )
    }
}