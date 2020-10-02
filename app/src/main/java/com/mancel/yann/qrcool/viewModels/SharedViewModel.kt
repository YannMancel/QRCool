package com.mancel.yann.qrcool.viewModels

import androidx.camera.core.CameraSelector
import androidx.lifecycle.*
import com.mancel.yann.qrcool.models.*
import com.mancel.yann.qrcool.repositories.DatabaseRepository
import com.mancel.yann.qrcool.states.CameraState
import com.mancel.yann.qrcool.states.DatabaseState
import com.mancel.yann.qrcool.utils.BarcodeTools
import com.mancel.yann.qrcool.utils.logCoroutineOnDebug
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

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

    private val _databaseState = MutableLiveData<DatabaseState>()

    private val _errorHandler = CoroutineExceptionHandler { _, throwable ->
        this.logCoroutineOnDebug(throwable.message)
        this.changeDatabaseStateToFailure(throwable.message)
    }

    companion object {
        private const val COROUTINE_ADD_DATA = "Add data in database"
        private const val COROUTINE_REMOVE_DATA = "Remove data in database"
    }

    // CONSTRUCTORS --------------------------------------------------------------------------------

    init {
        // By default, the FAB menu is closed
        this._isFABMenuOpen.value = false

        this._barcodes = this.configureMediatorLiveData()
    }

    // METHODS -------------------------------------------------------------------------------------

    // -- CoroutineContext --

    private fun getCoroutineContext(name: String): CoroutineContext =
        this._backgroundDispatcher + this._errorHandler + CoroutineName(name)

    // -- Barcode --

    /**
     * Configure the [MediatorLiveData] of [List] of [BarcodeOverlay].
     * It retrieves data from 5 sources.
     *  - [DatabaseRepository.getTextBarcodes]
     *  - [DatabaseRepository.getWifiBarcodes]
     *  - [DatabaseRepository.getUrlBarcodes]
     *  - [DatabaseRepository.getSMSBarcodes]
     *  - [DatabaseRepository.getGeoPointBarcodes]
     */
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
            context = this.getCoroutineContext(name = COROUTINE_ADD_DATA)
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

            this@SharedViewModel.changeDatabaseStateToSuccess()
        }

    fun removeBarcode(barcode: BarcodeOverlay) =
        this.viewModelScope.launch(
            context = this.getCoroutineContext(name = COROUTINE_REMOVE_DATA)
        ) {
            this@SharedViewModel.logCoroutineOnDebug("Launch started")

            when (barcode) {
                is TextBarcode -> this@SharedViewModel._databaseRepository.removeTextBarcodes(barcode)
                is WifiBarcode -> this@SharedViewModel._databaseRepository.removeWifiBarcodes(barcode)
                is UrlBarcode -> this@SharedViewModel._databaseRepository.removeUrlBarcodes(barcode)
                is SMSBarcode -> this@SharedViewModel._databaseRepository.removeSMSBarcodes(barcode)
                is GeoPointBarcode -> this@SharedViewModel._databaseRepository.removeGeoPointBarcodes(barcode)
            }

            this@SharedViewModel.changeDatabaseStateToSuccess()
        }

    // -- FAB Menu --

    fun isFABMenuOpen() : LiveData<Boolean> = this._isFABMenuOpen

    fun toogleFabMenu() {
        this._isFABMenuOpen.value = !this._isFABMenuOpen.value!!
    }

    // -- CameraState --

    fun getCameraState(): LiveData<CameraState> = this._cameraState

    fun changeCameraStateToSetupCamera() {
        this._cameraState.value = CameraState.SetupCamera(this._lensFacing)
    }

    fun changeCameraStateToPreviewReady() {
        this._cameraState.value = CameraState.PreviewReady(this._lensFacing)
    }

    fun changeCameraStateToError(errorMessage: String) {
        this._cameraState.value = CameraState.Error(errorMessage, this._lensFacing)
    }

    // -- DatabaseState --

    fun getDatabaseState(): LiveData<DatabaseState> = this._databaseState

    private fun changeDatabaseStateToSuccess() {
        this.logCoroutineOnDebug("Success")
        // In background
        this._databaseState.postValue(DatabaseState.Success)
    }

    private fun changeDatabaseStateToFailure(errorMessage: String?) {
        this.logCoroutineOnDebug("Failure")
        // In background
        this._databaseState.postValue(
            DatabaseState.Failure(_errorMessage = errorMessage ?: "-")
        )
    }
}