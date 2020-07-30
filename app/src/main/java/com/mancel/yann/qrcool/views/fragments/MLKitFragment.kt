package com.mancel.yann.qrcool.views.fragments

import android.util.Log
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import com.mancel.yann.qrcool.R
import kotlinx.android.synthetic.main.fragment_m_l_kit.view.*

/**
 * Created by Yann MANCEL on 29/07/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool.views.fragments
 *
 * A [BaseFragment] subclass.
 */
class MLKitFragment : BaseFragment() {

    /*
        See:
            [1]: https://developer.android.com/training/camerax/preview
     */

    // FIELDS --------------------------------------------------------------------------------------

    private lateinit var _cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
    private var _camera: Camera? = null

    // METHODS -------------------------------------------------------------------------------------

    // -- BaseFragment --

    override fun getFragmentLayout(): Int = R.layout.fragment_m_l_kit

    override fun configureDesign() = this.configureCameraX()

    override fun actionAfterPermission() = this.configureCameraX()

    // -- CameraX --

    /**
     * Configures CameraX with Camera permission
     */
    private fun configureCameraX() {
        if (this.hasCameraPermission()) {
            this.configureCameraProvider()
        }
    }

    /**
     * Configures the [ProcessCameraProvider] of CameraX
     */
    private fun configureCameraProvider() {
        this._cameraProviderFuture = ProcessCameraProvider.getInstance(this.requireContext())

        this._cameraProviderFuture.addListener(
            Runnable {
                val cameraProvider = this._cameraProviderFuture.get()
                this.bindPreview(cameraProvider)
            },
            ContextCompat.getMainExecutor(this.requireContext())
        )
    }

    /**
     * Binds the use case, called [Preview], to the Fragment's lifecycle
     * @param cameraProvider a [ProcessCameraProvider]
     */
    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        // Use case: Preview
        val preview = Preview.Builder()
            .build()

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        // Connects Preview to the view into xml file
        preview.setSurfaceProvider(
            this._rootView.fragment_m_l_kit_camera.createSurfaceProvider()
        )

        try {
            // Binds Preview to the Fragment's lifecycle
            this._camera = cameraProvider.bindToLifecycle(
                this.viewLifecycleOwner,
                cameraSelector,
                preview
            )
        } catch(e: Exception) {
            Log.e(
                this.javaClass.simpleName,
                "Use case binding failed: ${e.message}"
            )
        }
    }
}