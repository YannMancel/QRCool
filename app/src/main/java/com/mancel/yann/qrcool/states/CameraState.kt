package com.mancel.yann.qrcool.states

import com.mancel.yann.qrcool.views.fragments.CameraXFragment

/**
 * Created by Yann MANCEL on 04/08/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool.states
 */
sealed class CameraState {
    
    // FIELDS --------------------------------------------------------------------------------------

    abstract val _lensFacing: Int

    // CLASSES -------------------------------------------------------------------------------------

    /**
     * State:  SetupCamera
     * Where:  [CameraXFragment.onViewCreated]
     * Why:    After post method of Preview widget
     */
    class SetupCamera(
        override val _lensFacing: Int
    ) : CameraState()

    /**
     * State:  PreviewReady
     * Where:  [CameraXFragment.configureCameraProviderOfCameraX]
     * Why:    After to bind Preview use case
     */
    class PreviewReady(
        override val _lensFacing: Int
    ) : CameraState()

    /**
     * State:  Error
     * Where:  [CameraXFragment.configureCameraX]
     * Why:    No permission
     */
    class Error(
        val _errorMessage: String,
        override val _lensFacing: Int
    ) : CameraState()
}