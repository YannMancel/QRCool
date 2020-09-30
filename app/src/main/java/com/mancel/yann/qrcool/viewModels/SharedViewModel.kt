package com.mancel.yann.qrcool.viewModels

import androidx.camera.core.CameraSelector
import androidx.lifecycle.*
import com.mancel.yann.qrcool.models.*
import com.mancel.yann.qrcool.repositories.DatabaseRepository
import com.mancel.yann.qrcool.states.CameraState
import com.mancel.yann.qrcool.utils.BarcodeTools
import com.mancel.yann.qrcool.utils.logCoroutineOnDebug
import kotlinx.coroutines.*

/**
 * Created by Yann MANCEL on 22/07/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool.viewModels
 *
 * A [ViewModel] subclass.
 */
class SharedViewModel(
    private val _databaseRepository: DatabaseRepository,
    private val _backgroundDispatcher: CoroutineDispatcher = Dispatchers.IO
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

    /** Configure the [MediatorLiveData] of [List] of [BarcodeOverlay] */
    private fun configureMediatorLiveData(): MediatorLiveData<List<BarcodeOverlay>> {
        return MediatorLiveData<List<BarcodeOverlay>>().also {
            it.addSource(this._databaseRepository.getTextBarcodes().asLiveData()) { textBarcodes ->
                it.value =
                    BarcodeTools.combineDataFromSeveralLiveData(
                        it.value,
                        textBarcodes,
                        TextBarcode::class.java
                    )
            }

            it.addSource(this._databaseRepository.getWifiBarcodes().asLiveData()) { wifiBarcodes ->
                it.value =
                    BarcodeTools.combineDataFromSeveralLiveData(
                        it.value,
                        wifiBarcodes,
                        WifiBarcode::class.java
                    )
            }

            it.addSource(this._databaseRepository.getUrlBarcodes().asLiveData()) { urlBarcodes ->
                it.value =
                    BarcodeTools.combineDataFromSeveralLiveData(
                        it.value,
                        urlBarcodes,
                        UrlBarcode::class.java
                    )
            }

            it.addSource(this._databaseRepository.getSMSBarcodes().asLiveData()) { smsBarcodes ->
                it.value =
                    BarcodeTools.combineDataFromSeveralLiveData(
                        it.value,
                        smsBarcodes,
                        SMSBarcode::class.java
                    )
            }

            it.addSource(this._databaseRepository.getGeoPointBarcodes().asLiveData()) { geoPointBarcodes ->
                it.value =
                    BarcodeTools.combineDataFromSeveralLiveData(
                        it.value,
                        geoPointBarcodes,
                        GeoPointBarcode::class.java
                    )
            }
        }
    }

    fun getBarcodes(): LiveData<List<BarcodeOverlay>> = this._barcodes

    fun addBarcodes(barcodes: List<BarcodeOverlay>) =
        this.viewModelScope.launch(
            context = this._backgroundDispatcher + CoroutineName("Add data in database")
        ) {
            this@SharedViewModel.logCoroutineOnDebug("Launch started")

            // Filter to have only barcodes that are really new
            val barcodesToAdd = barcodes.filterNot { newBarcode ->
                this@SharedViewModel._barcodes.value?.any { currentBarcode ->
                    newBarcode._rawValue == currentBarcode._rawValue
                } ?: false
            }

            barcodesToAdd.forEach {
                when (it) {
                    is TextBarcode -> this@SharedViewModel._databaseRepository.insertTextBarcodes(it)
                    is WifiBarcode -> this@SharedViewModel._databaseRepository.insertWifiBarcodes(it)
                    is UrlBarcode -> this@SharedViewModel._databaseRepository.insertUrlBarcodes(it)
                    is SMSBarcode -> this@SharedViewModel._databaseRepository.insertSMSBarcodes(it)
                    is GeoPointBarcode -> this@SharedViewModel._databaseRepository.insertGeoPointBarcodes(it)
                }
            }
        }

    fun removeBarcode(barcode: BarcodeOverlay) =
        this.viewModelScope.launch(
            context = this._backgroundDispatcher + CoroutineName("Remove data in database")
        ) {
            this@SharedViewModel.logCoroutineOnDebug("Launch started")

            when (barcode) {
                is TextBarcode -> this@SharedViewModel._databaseRepository.removeTextBarcodes(barcode)
                is WifiBarcode -> this@SharedViewModel._databaseRepository.removeWifiBarcodes(barcode)
                is UrlBarcode -> this@SharedViewModel._databaseRepository.removeUrlBarcodes(barcode)
                is SMSBarcode -> this@SharedViewModel._databaseRepository.removeSMSBarcodes(barcode)
                is GeoPointBarcode -> this@SharedViewModel._databaseRepository.removeGeoPointBarcodes(barcode)
            }
        }

    // -- FAB Menu --

    fun isFABMenuOpen() : LiveData<Boolean> = this._isFABMenuOpen

    fun toogleFabMenu() {
        this._isFABMenuOpen.value = !this._isFABMenuOpen.value!!
    }

    // -- CameraState --

    fun getCameraState(): LiveData<CameraState> = this._cameraState

    fun changeCameraStateToSetupCamera() {
        this._cameraState.value = CameraState.SetupCamera(
            this._lensFacing
        )
    }

    fun changeCameraStateToPreviewReady() {
        this._cameraState.value = CameraState.PreviewReady(
            this._lensFacing
        )
    }

    fun changeCameraStateToError(errorMessage: String) {
        this._cameraState.value = CameraState.Error(
            errorMessage,
            this._lensFacing
        )
    }
}