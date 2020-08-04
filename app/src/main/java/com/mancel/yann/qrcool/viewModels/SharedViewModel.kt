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

    private val _qRCodes = MutableLiveData<List<QRCode>>()
    private val _isFABMenuOpen = MutableLiveData<Boolean>()

    private val _cameraState = MutableLiveData<CameraState>()
    private var _lensFacing: Int = CameraSelector.LENS_FACING_BACK

    // CONSTRUCTORS --------------------------------------------------------------------------------

    init {
        // By default, the FAB menu is closed
        this._isFABMenuOpen.value = false
    }

    // METHODS -------------------------------------------------------------------------------------

    // -- QR Code --

    /**
     * Gets the [LiveData] of [List] of [QRCode]
     */
    fun getQRCodes(): LiveData<List<QRCode>> = this._qRCodes

    /**
     * Adds a new value to the QRCodes
     */
    fun addQRCode(textOfQRCode: String) {
        val currentData =  this._qRCodes.value?.toMutableList() ?: mutableListOf()

        val newQRCode = QRCode(textOfQRCode)

        if (!currentData.contains(newQRCode)) {
            currentData.add(newQRCode)
        }

        // Notify
        this._qRCodes.value = currentData
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