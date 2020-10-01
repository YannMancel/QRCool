package com.mancel.yann.qrcool.states

import com.mancel.yann.qrcool.viewModels.SharedViewModel

/**
 * Created by Yann MANCEL on 01/10/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool.states
 */
sealed class DatabaseState {

    // CLASSES -------------------------------------------------------------------------------------

    /**
     * State:  Success
     * Where:  [SharedViewModel.addBarcodes] & [SharedViewModel.removeBarcode]
     * Why:    Even is a success
     */
    object Success : DatabaseState()

    /**
     * State:  Failure
     * Where:  [SharedViewModel]
     * Why:    Event is a failure
     */
    class Failure(val _errorMessage: String) : DatabaseState()
}