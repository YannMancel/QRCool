package com.mancel.yann.qrcool.states

/**
 * Created by Yann MANCEL on 04/08/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool.states
 */
sealed class CameraState(
    val _lensFacing: Int
) {

    // CLASSES -------------------------------------------------------------------------------------

    /**
     * State:  SetupCamera
     * Where:  MLKitFragment#onViewCreated
     * Why:    After post method of Preview widget
     */
    class SetupCamera(
        lensFacing: Int
    ) : CameraState(lensFacing)

    /**
     * State:  PreviewReady
     * Where:  MLKitFragment#configureCameraProvider
     * Why:    After to bind Preview use case
     */
    class PreviewReady(
        lensFacing: Int
    ) : CameraState(lensFacing)

    /**
     * State:  Error
     * Where:  MLKitFragment#configureCameraX
     * Why:    No permission
     */
    class Error(
        val _errorMessage: String,
        lensFacing: Int
    ) : CameraState(lensFacing)
}