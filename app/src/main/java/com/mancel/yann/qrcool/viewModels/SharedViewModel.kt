package com.mancel.yann.qrcool.viewModels

import androidx.camera.core.CameraSelector
import androidx.lifecycle.*
import com.mancel.yann.qrcool.models.*
import com.mancel.yann.qrcool.repositories.DatabaseRepository
import com.mancel.yann.qrcool.states.CameraState
import com.mancel.yann.qrcool.utils.BarcodeTools
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by Yann MANCEL on 22/07/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool.viewModels
 *
 * A [ViewModel] subclass.
 */
class SharedViewModel(
    private val _databaseRepository: DatabaseRepository
) :  ViewModel() {

    // FIELDS --------------------------------------------------------------------------------------

    private val _barcodes: MediatorLiveData<List<BarcodeOverlay>>

    private val _isFABMenuOpen = MutableLiveData<Boolean>()

    private val _cameraState = MutableLiveData<CameraState>()
    private var _lensFacing: Int = CameraSelector.LENS_FACING_BACK

    // CONSTRUCTORS --------------------------------------------------------------------------------

    init {
        // By default, the FAB menu is closed
        this._isFABMenuOpen.value = false

        this._barcodes = this.configureMediatorLiveData()
    }

    // METHODS -------------------------------------------------------------------------------------

    // -- Barcode --

    /**
     * Configure the [MediatorLiveData] of [List] of [BarcodeOverlay]
     */
    private fun configureMediatorLiveData(): MediatorLiveData<List<BarcodeOverlay>> {
        return MediatorLiveData<List<BarcodeOverlay>>().also {
            it.addSource(this._databaseRepository.getTextBarcodes()) { textBarcodes ->
                it.value =
                    BarcodeTools.combineDataFromSeveralLiveData(
                        it.value,
                        textBarcodes,
                        TextBarcode::class.java
                    )
            }

            it.addSource(this._databaseRepository.getWifiBarcodes()) { wifiBarcodes ->
                it.value =
                    BarcodeTools.combineDataFromSeveralLiveData(
                        it.value,
                        wifiBarcodes,
                        WifiBarcode::class.java
                    )
            }

            it.addSource(this._databaseRepository.getUrlBarcodes()) { urlBarcodes ->
                it.value =
                    BarcodeTools.combineDataFromSeveralLiveData(
                        it.value,
                        urlBarcodes,
                        UrlBarcode::class.java
                    )
            }

            it.addSource(this._databaseRepository.getSMSBarcodes()) { smsBarcodes ->
                it.value =
                    BarcodeTools.combineDataFromSeveralLiveData(
                        it.value,
                        smsBarcodes,
                        SMSBarcode::class.java
                    )
            }

            it.addSource(this._databaseRepository.getGeoPointBarcodes()) { geoPointBarcodes ->
                it.value =
                    BarcodeTools.combineDataFromSeveralLiveData(
                        it.value,
                        geoPointBarcodes,
                        GeoPointBarcode::class.java
                    )
            }
        }
    }

    /**
     * Gets the [LiveData] of [List] of [BarcodeOverlay]
     */
    fun getBarcodes(): LiveData<List<BarcodeOverlay>> = this._barcodes

    /**
     * Adds barcodes to the [MutableLiveData] of [List] of [BarcodeOverlay]
     * @param barcodes a [List] of [BarcodeOverlay]
     */
    fun addBarcodes(barcodes: List<BarcodeOverlay>) = viewModelScope.launch(context = Dispatchers.IO) {
        // Filter to have only barcodes that are really new
        val barcodeToAdd = barcodes.filterNot { newBarcode ->
            this@SharedViewModel._barcodes.value?.any { currentBarcode ->
                newBarcode._rawValue == currentBarcode._rawValue
            } ?: false
        }

        barcodeToAdd.forEach {
            when (it) {
                is TextBarcode -> this@SharedViewModel._databaseRepository.insertTextBarcodes(it)
                is WifiBarcode -> this@SharedViewModel._databaseRepository.insertWifiBarcodes(it)
                is UrlBarcode -> this@SharedViewModel._databaseRepository.insertUrlBarcodes(it)
                is SMSBarcode -> this@SharedViewModel._databaseRepository.insertSMSBarcodes(it)
                is GeoPointBarcode -> this@SharedViewModel._databaseRepository.insertGeoPointBarcodes(it)
            }
        }
    }

    /**
     * Removes a barcode to the [MutableLiveData] of [List] of [BarcodeOverlay]
     * @param barcode a [BarcodeOverlay]
     */
    fun removeBarcode(barcode: BarcodeOverlay) = viewModelScope.launch(context = Dispatchers.IO) {
        when (barcode) {
            is TextBarcode -> this@SharedViewModel._databaseRepository.removeTextBarcodes(barcode)
            is WifiBarcode -> this@SharedViewModel._databaseRepository.removeWifiBarcodes(barcode)
            is UrlBarcode -> this@SharedViewModel._databaseRepository.removeUrlBarcodes(barcode)
            is SMSBarcode -> this@SharedViewModel._databaseRepository.removeSMSBarcodes(barcode)
            is GeoPointBarcode -> this@SharedViewModel._databaseRepository.removeGeoPointBarcodes(barcode)
        }
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